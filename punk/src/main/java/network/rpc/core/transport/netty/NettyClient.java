package network.rpc.core.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import network.rpc.core.transport.InFlightRequests;
import network.rpc.core.transport.Transport;
import network.rpc.core.transport.TransportClient;
import network.rpc.core.transport.netty.codec.RequestEncoder;
import network.rpc.core.transport.netty.codec.ResponseDecoder;
import network.rpc.core.transport.netty.handler.ResponseInvocation;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class NettyClient implements TransportClient {

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private final InFlightRequests inFlightRequests;
    private final List<Channel> channels = new LinkedList<>();

    public NettyClient() {
        inFlightRequests = new InFlightRequests();
    }

    @Override
    public Transport createTransport(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        return new NettyTransport(createChannel(address, connectionTimeout), inFlightRequests);
    }

    @Override
    public void close() {
        for (Channel channel : channels) {
            if (channel != null) {
                channel.close();
            }
        }

        if (group != null) {
            group.shutdownGracefully();
        }
        inFlightRequests.close();
    }

    private Bootstrap createBootstrap(ChannelHandler channelHandler, EventLoopGroup group) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(group)
                .handler(channelHandler)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return bootstrap;
    }

    private synchronized Channel createChannel(SocketAddress address, long connectionTimeout) throws TimeoutException, InterruptedException {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null");
        }

        if (group == null) {
            group = createGroup();
        }

        if (bootstrap == null) {
            ChannelHandler channelHandler = createChannelHandler();
            bootstrap = createBootstrap(channelHandler, group);
        }

        ChannelFuture channelFuture = bootstrap.connect(address);

        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }

        Channel channel = channelFuture.channel();

        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }

        channels.add(channel);
        return channel;
    }

    private EventLoopGroup createGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
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