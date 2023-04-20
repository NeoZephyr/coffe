package jubi.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import jubi.netty.handler.TransportFrameDecoder;
import jubi.netty.handler.TransportResponseHandler;
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

@Slf4j
public class TransportClientFactory implements Closeable {

    private static class ClientPool {
        TransportClient[] clients;
        Object[] locks;

        ClientPool(int size) {
            clients = new TransportClient[size];
            locks = new Object[size];

            for (int i = 0; i < size; ++i) {
                locks[i] = new Object();
            }
        }
    }

    private Random rand;
    private TransportContext context;
    private TransportConf conf;
    private ConcurrentHashMap<SocketAddress, ClientPool> connectionPool;
    private int numConnectionsPerPeer;
    private Class<? extends Channel> socketChannelClass;
    private EventLoopGroup workerGroup;
    private PooledByteBufAllocator allocator;

    public TransportClientFactory(TransportContext context) {
        this.context = Objects.requireNonNull(context);
        this.conf = context.getConf();
        this.connectionPool = new ConcurrentHashMap<>();
        this.numConnectionsPerPeer = conf.numConnectionsPerPeer();
        this.rand = new Random();
        IOMode ioMode = conf.ioMode();
        this.socketChannelClass = NettyUtils.getClientChannelClass(ioMode);
        this.workerGroup = NettyUtils.createEventLoop(ioMode, conf.clientThreads(), "netty-rpc-client");
        this.allocator = NettyUtils.createPooledByteBufAllocator(conf.preferDirectBuffer(), false, conf.clientThreads());
    }

    public TransportClient createClient(String host, int port) throws IOException, InterruptedException {
        return createClient(host, port, -1);
    }

    public TransportClient createClient(String host, int port, int partitionId) throws IOException, InterruptedException {
        return createClient(host, port, partitionId, new TransportFrameDecoder());
    }

    public TransportClient createClient(String host, int port, int partitionId, ChannelInboundHandlerAdapter decoder) throws IOException, InterruptedException {
        InetSocketAddress unresolvedAddress = InetSocketAddress.createUnresolved(host, port);
        ClientPool clientPool = connectionPool.computeIfAbsent(unresolvedAddress, x -> new ClientPool(numConnectionsPerPeer));
        int index = partitionId < 0 ? rand.nextInt(numConnectionsPerPeer) : partitionId % numConnectionsPerPeer;
        TransportClient client = clientPool.clients[index];

        if (client != null && client.isActive()) {
            TransportResponseHandler handler = client.getChannel().pipeline().get(TransportResponseHandler.class);

            if (client.isActive()) {
                log.info("Returning cached connection to {}: {}", client.getSocketAddress(), client);
                return client;
            }
        }

        long preResolveHost = System.nanoTime();
        InetSocketAddress resolvedAddress = new InetSocketAddress(host, port);
        long hostResolveTimeMs = (System.nanoTime() - preResolveHost) / 1000000;

        if (hostResolveTimeMs > 2000) {
            log.warn("DNS resolution for {} took {} ms", resolvedAddress, hostResolveTimeMs);
        }

        synchronized (clientPool.locks[index]) {
            client = clientPool.clients[index];

            if (client != null) {
                if (client.isActive()) {
                    log.info("Returning cached connection to {}: {}", resolvedAddress, client);
                    return client;
                } else {
                    log.info("Found inactive connection to {}, creating a new one.", resolvedAddress);
                }
            }

            clientPool.clients[index] = internalCreateClient(resolvedAddress, decoder);
            return clientPool.clients[index];
        }
    }

    private TransportClient internalCreateClient(InetSocketAddress address, ChannelInboundHandlerAdapter decoder) throws InterruptedException, IOException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(socketChannelClass)
                .option(ChannelOption.TCP_NODELAY, true)
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
                TransportResponseHandler transportResponseHandler = context.initializePipeline(ch, decoder);
                TransportClient client = new TransportClient(ch, transportResponseHandler);
                clientRef.set(client);
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