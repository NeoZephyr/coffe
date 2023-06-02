package jubi.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import jubi.netty.TransportConf;
import jubi.netty.TransportContext;
import jubi.netty.handler.TransportChannelHandler;
import jubi.netty.util.IOMode;
import jubi.netty.util.NettyUtils;
import jubi.netty.util.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * config:
 *  1. pool size
 *  2. tcp param
 *
 * state control
 */
@Slf4j
public class TransportClientFactory implements Closeable {

    private static class ClientPool {
        TransportClient[] clients;
        Object[] locks;
        volatile long lastConnectFailed;

        ClientPool(int size) {
            clients = new TransportClient[size];
            locks = new Object[size];

            for (int i = 0; i < size; ++i) {
                locks[i] = new Object();
            }

            lastConnectFailed = 0;
        }
    }

    /** Random number generator for picking connections between peers. */
    private final Random rand;
    private final TransportContext context;
    private final TransportConf conf;
    private final int fastFailTimeWindow;
    private final ConcurrentHashMap<SocketAddress, ClientPool> connectionPool;
    private final int numConnectionsPerPeer;
    private final Class<? extends Channel> socketChannelClass;
    private final EventLoopGroup workerGroup;
    private final PooledByteBufAllocator allocator;

    public TransportClientFactory(TransportContext context) {
        this.context = Objects.requireNonNull(context);
        this.conf = context.getConf();
        this.connectionPool = new ConcurrentHashMap<>();
        this.numConnectionsPerPeer = conf.numConnectionsPerPeer();
        this.rand = new Random();
        this.fastFailTimeWindow = conf.connectRetryWaitMs();

        IOMode ioMode = conf.ioMode();
        this.socketChannelClass = NettyUtils.getClientChannelClass(ioMode);
        this.workerGroup = NettyUtils.createEventLoop(ioMode, conf.clientThreads(), "netty-rpc-client");
        this.allocator = NettyUtils.createPooledByteBufAllocator(conf.preferDirectBuffer(), false, conf.clientThreads());
    }

    public TransportClient createClient(String host, int port) throws InterruptedException, IOException {
        return createClient(host, port, false);
    }

    public TransportClient createClient(String host, int port, boolean fastFail) throws IOException, InterruptedException {
        // Use unresolved address here to avoid DNS resolution each time we creates a client.
        InetSocketAddress unresolvedAddress = InetSocketAddress.createUnresolved(host, port);
        ClientPool clientPool = connectionPool.computeIfAbsent(unresolvedAddress, x -> new ClientPool(numConnectionsPerPeer));
        int index = rand.nextInt(numConnectionsPerPeer);
        TransportClient client = clientPool.clients[index];

        if (client != null && client.isActive()) {
            TransportChannelHandler handler = client.getChannel().pipeline().get(TransportChannelHandler.class);

            // Make sure that the channel will not timeout by updating the last use time of the handler
            synchronized (handler) {
                handler.getResponseProcessor().updateLastRequestTime();
            }

            if (client.isActive()) {
                log.trace("Returning cached connection to {}: {}", client.getSocketAddress(), client);
                return client;
            }
        }

        long preResolveHost = System.nanoTime();
        InetSocketAddress resolvedAddress = new InetSocketAddress(host, port);
        long hostResolveTimeMs = (System.nanoTime() - preResolveHost) / 1000000;
        String resolveMsg = resolvedAddress.isUnresolved() ? "failed" : "succeed";

        if (hostResolveTimeMs > 2000) {
            log.warn("DNS resolution {} for {} took {} ms", resolveMsg, resolvedAddress, hostResolveTimeMs);
        }

        synchronized (clientPool.locks[index]) {
            client = clientPool.clients[index];

            if (client != null) {
                if (client.isActive()) {
                    log.debug("Returning cached connection to {}: {}", resolvedAddress, client);
                    return client;
                } else {
                    log.info("Found inactive connection to {}, creating a new one.", resolvedAddress);
                }
            }

            if (fastFail && System.currentTimeMillis() - clientPool.lastConnectFailed < fastFailTimeWindow) {
                throw new IOException(String.format("Connecting to %s failed in the last %s ms, fail this connection directly",
                        resolvedAddress, fastFailTimeWindow));
            }

            try {
                clientPool.clients[index] = doCreateClient(resolvedAddress);
                clientPool.lastConnectFailed = 0;
            } catch (IOException e) {
                clientPool.lastConnectFailed = System.currentTimeMillis();
                throw e;
            }

            return clientPool.clients[index];
        }
    }

    public TransportClient createUnmanagedClient(String host, int port) throws IOException, InterruptedException {
        final InetSocketAddress address = new InetSocketAddress(host, port);
        return doCreateClient(address);
    }

    private TransportClient doCreateClient(InetSocketAddress address) throws InterruptedException, IOException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(socketChannelClass)
                .option(ChannelOption.TCP_NODELAY, true) // Disable Nagle's Algorithm since we don't want packets to wait
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, conf.connectTimeoutMs())
                .option(ChannelOption.ALLOCATOR, allocator);

        if (conf.receiveBuf() > 0) {
            bootstrap.option(ChannelOption.SO_RCVBUF, conf.receiveBuf());
        }

        if (conf.sendBuf() > 0) {
            bootstrap.option(ChannelOption.SO_SNDBUF, conf.sendBuf());
        }

        AtomicReference<TransportClient> clientRef = new AtomicReference<>();
        AtomicReference<Channel> channelRef = new AtomicReference<>();

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                TransportChannelHandler handler = context.initializePipeline(ch);
                clientRef.set(handler.getClient());
                channelRef.set(ch);
            }
        });

        ChannelFuture future = bootstrap.connect(address);

        if (!future.await(conf.connectTimeoutMs())) {
            throw new IOException(String.format("Connecting to %s timed out (%s ms)", address, conf.connectTimeoutMs()));
        } else if (future.cause() != null) {
            throw new IOException(String.format("Failed to connect to %s", address), future.cause());
        }

        TransportClient client = clientRef.get();
        assert client != null : "Channel future completed successfully with null client";

        log.info("Connection to {} successful", address);

        return client;
    }

    @Override
    public void close() throws IOException {
        for (ClientPool pool : connectionPool.values()) {
            for (int i = 0; i < pool.clients.length; ++i) {
                TransportClient client = pool.clients[i];

                if (client != null) {
                    pool.clients[i] = null;
                    Utils.closeQuietly(client);
                }
            }
        }
        connectionPool.clear();

        if (workerGroup != null && !workerGroup.isShuttingDown()) {
            workerGroup.shutdownGracefully();
        }
    }

    public TransportContext getContext() {
        return context;
    }
}