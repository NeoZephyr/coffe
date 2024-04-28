package network.rpc.core.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import network.rpc.core.transport.RequestHandlerRegistry;
import network.rpc.core.transport.TransportServer;
import network.rpc.core.transport.netty.codec.RequestDecoder;
import network.rpc.core.transport.netty.codec.ResponseEncoder;
import network.rpc.core.transport.netty.handler.RequestInvocation;

@Slf4j
public class NettyServer implements TransportServer {

    private int port;
    private EventLoopGroup acceptGroup;
    private EventLoopGroup ioGroup;
    private Channel channel;
    private RequestHandlerRegistry registry;

    @Override
    public void start(RequestHandlerRegistry registry, int port) throws Exception {
        this.port = port;
        this.registry = registry;
        this.acceptGroup = createGroup();
        this.ioGroup = createGroup();
        ChannelHandler channelHandler = createChannelHandler();
        ServerBootstrap bootstrap = createBootstrap(channelHandler, acceptGroup, ioGroup);
        this.channel = doBind(bootstrap);
    }

    @Override
    public void stop() {
        if (acceptGroup != null) {
            acceptGroup.shutdownGracefully();
        }

        if (ioGroup != null) {
            ioGroup.shutdownGracefully();
        }

        if (channel != null) {
            channel.close();
        }
    }

    private ServerBootstrap createBootstrap(ChannelHandler channelHandler, EventLoopGroup acceptGroup, EventLoopGroup ioGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(acceptGroup, ioGroup)
                .childHandler(channelHandler)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return serverBootstrap;
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
            protected void initChannel(Channel ch) throws Exception {
                channel.pipeline()
                        .addLast(new RequestDecoder())
                        .addLast(new ResponseEncoder())
                        .addLast(new RequestInvocation(registry));
            }
        };
    }

    private Channel doBind(ServerBootstrap serverBootstrap) throws Exception {
        return serverBootstrap.bind(port)
                .sync()
                .channel();
    }
}