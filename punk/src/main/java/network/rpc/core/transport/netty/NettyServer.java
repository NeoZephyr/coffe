package network.rpc.core.transport.netty;

import common.IOMode;
import common.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jubi.netty.util.Utils;
import lombok.extern.slf4j.Slf4j;
import network.rpc.core.transport.RequestHandlerRegistry;
import network.rpc.core.transport.TransportServer;
import network.rpc.core.transport.netty.codec.RequestDecoder;
import network.rpc.core.transport.netty.codec.ResponseEncoder;
import network.rpc.core.transport.netty.handler.RequestInvocation;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer implements TransportServer {

    private int port;
    private EventLoopGroup acceptGroup;
    private EventLoopGroup ioGroup;
    private Channel channel;
    private RequestHandlerRegistry registry;
    private final IOMode ioMode;
    private final int acceptorThreads;
    private final int ioThreads;

    public NettyServer() {
        ioMode = IOMode.NIO;
        acceptorThreads = 1;
        ioThreads = 1;
    }

    public NettyServer(RequestHandlerRegistry registry, int port) {
        ioMode = IOMode.NIO;
        acceptorThreads = 1;
        ioThreads = 1;

        boolean close = true;

        try {
            start(registry, port);
            close = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (close) {
                // Utils.closeQuietly(this);
                stop();
            }
        }
    }

    @Override
    public void start(RequestHandlerRegistry registry, int port) throws Exception {
        this.port = port;
        this.registry = registry;
        this.acceptGroup = NettyUtils.createEventLoop(ioMode, 1, "acceptor-thread");
        this.ioGroup = NettyUtils.createEventLoop(ioMode, 1, "io-thread");
        ChannelHandler channelHandler = createChannelHandler();
        ServerBootstrap bootstrap = createBootstrap(channelHandler, acceptGroup, ioGroup);
        this.channel = doBind(bootstrap);
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();

        log.trace("Server started on {} with port {}", localAddress.getHostString(), port);
    }

    @Override
    public void stop() {
        if (acceptGroup != null) {
            // bootstrap.config().group()
            acceptGroup.shutdownGracefully();
            acceptGroup = null;
        }

        if (ioGroup != null) {
            // bootstrap.config().childGroup()
            ioGroup.shutdownGracefully();
            ioGroup = null;
        }

        if (channel != null) {
            channel.close();
            // channel.close().awaitUninterruptibly(10, TimeUnit.SECONDS);
            channel = null;
        }
    }

    private ServerBootstrap createBootstrap(ChannelHandler channelHandler, EventLoopGroup acceptGroup, EventLoopGroup ioGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NettyUtils.getServerChannelClass(ioMode))
                .group(acceptGroup, ioGroup)
                .childHandler(channelHandler)
                .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, !SystemUtils.IS_OS_WINDOWS)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // bootstrap.option(ChannelOption.SO_BACKLOG, conf.backlog());
        // bootstrap.childOption(ChannelOption.SO_RCVBUF, conf.receiveBuf());
        // bootstrap.childOption(ChannelOption.SO_SNDBUF, conf.sendBuf());
        // bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        return serverBootstrap;
    }

    private ChannelHandler createChannelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                log.trace("New connection accepted for remote address {}.", ch.remoteAddress());

                channel.pipeline()
                        .addLast(new RequestDecoder())
                        .addLast(new ResponseEncoder())
                        .addLast(new RequestInvocation(registry));
            }
        };
    }

    private Channel doBind(ServerBootstrap serverBootstrap) throws Exception {
        // InetSocketAddress address = host == null ? new InetSocketAddress(port): new InetSocketAddress(host, port);
        // bind address

        return serverBootstrap.bind(port)
                .syncUninterruptibly()
                .channel();
    }
}