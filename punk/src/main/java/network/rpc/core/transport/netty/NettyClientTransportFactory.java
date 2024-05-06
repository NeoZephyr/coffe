package network.rpc.core.transport.netty;

import common.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import common.IOMode;
import jubi.netty.util.Utils;
import lombok.extern.slf4j.Slf4j;
import network.rpc.core.transport.ClientTransportFactory;
import network.rpc.core.transport.InFlightRequests;
import network.rpc.core.transport.Transport;
import network.rpc.core.transport.netty.codec.RequestEncoder;
import network.rpc.core.transport.netty.codec.ResponseDecoder;
import network.rpc.core.transport.netty.handler.ResponseInvocation;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
public class NettyClientTransportFactory implements ClientTransportFactory {

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private final InFlightRequests inFlightRequests;

    private final ConcurrentHashMap<SocketAddress, TransportPool> pools;
    private final int connectionsPerPeer;
    private final int connectTimeoutMillis;
    private final int workerThreads;
    private final IOMode ioMode;
    private final Random rand;

    private static class TransportPool {
        Transport[] transports;
        Object[] locks;
        volatile long lastConnectFailed;

        TransportPool(int size) {
            transports = new Transport[size];
            locks = new Object[size];

            for (int i = 0; i < size; ++i) {
                locks[i] = new Object();
            }

            lastConnectFailed = 0;
        }
    }

    public NettyClientTransportFactory() {
        inFlightRequests = new InFlightRequests();
        pools = new ConcurrentHashMap<>();
        rand = new Random();

        // read by transport configuration
        connectionsPerPeer = 1;
        connectTimeoutMillis = 0;
        workerThreads = 1;
        ioMode = IOMode.NIO;
    }

    /**
     * 1. check DNS resolve and resolve time
     *
     * InetSocketAddress resolvedAddress = new InetSocketAddress(host, port);
     * resolvedAddress.isUnresolved();
     *
     * 2. retry connect and backoff
     */
    @Override
    public Transport createTransport(SocketAddress address) throws IOException, InterruptedException, TimeoutException {
        TransportPool pool = pools.computeIfAbsent(address, x -> new TransportPool(connectionsPerPeer));
        int index = rand.nextInt(connectionsPerPeer);
        Transport transport = pool.transports[index];

        if (transport != null && transport.isActive()) {
            return transport;
        }

        synchronized (pool.locks[index]) {
            transport = pool.transports[index];

            if (transport != null) {
                if (transport.isActive()) {
                    return transport;
                } else {
                    log.info("Found inactive connection to {}, creating a new one", address);
                }
            }

            pool.transports[index] = doCreateTransport(address);
            return pool.transports[index];
        }
    }

    public Transport doCreateTransport(SocketAddress address) throws InterruptedException, TimeoutException, IOException {
        return new NettyTransport(createChannel(address), inFlightRequests);
    }

    @Override
    public void close() {
        for (TransportPool pool : pools.values()) {
            for (int i = 0; i < pool.transports.length; ++i) {
                Transport transport = pool.transports[i];

                if (transport != null) {
                    pool.transports[i] = null;
                    Utils.closeQuietly(transport);
                }
            }
        }

        pools.clear();

        if (group != null && !group.isShuttingDown()) {
            group.shutdownGracefully();
        }

        inFlightRequests.close();
    }

    private Bootstrap createBootstrap(ChannelHandler channelHandler, EventLoopGroup group) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NettyUtils.getClientChannelClass(ioMode))
                .group(group)
                .handler(channelHandler)
                .option(ChannelOption.TCP_NODELAY, true) // Disable Nagle's Algorithm since we don't want packets to wait
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // bootstrap.option(ChannelOption.SO_RCVBUF, conf.receiveBuf());
        // bootstrap.option(ChannelOption.SO_SNDBUF, conf.sendBuf());

        return bootstrap;
    }

    private synchronized Channel createChannel(SocketAddress address) throws TimeoutException, InterruptedException, IOException {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null");
        }

        if (group == null) {
            group = NettyUtils.createEventLoop(ioMode, workerThreads, "netty-rpc-client");
        }

        if (bootstrap == null) {
            ChannelHandler channelHandler = createChannelHandler();
            bootstrap = createBootstrap(channelHandler, group);
        }

        ChannelFuture channelFuture = bootstrap.connect(address);

        if (!channelFuture.await(connectTimeoutMillis)) {
            throw new TimeoutException(String.format("Connecting to %s timed out %sms", address, connectTimeoutMillis));
        } else if (channelFuture.cause() != null) {
            throw new IOException(String.format("Failed to connect to %s", address), channelFuture.cause());
        }

        Channel channel = channelFuture.channel();

        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }

        log.info("Connection to {} successful", address);

        return channel;
    }

    private ChannelHandler createChannelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline()
                        .addLast(new ResponseDecoder())
                        .addLast(new RequestEncoder())
                        .addLast(new ResponseInvocation(inFlightRequests));
            }
        };
    }
}