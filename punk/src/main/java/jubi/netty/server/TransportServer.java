package jubi.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import jubi.netty.TransportConf;
import jubi.netty.TransportContext;
import jubi.netty.util.IOMode;
import jubi.netty.util.NettyUtils;
import jubi.netty.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TransportServer implements Closeable {

    private TransportContext context;
    private TransportConf conf;
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;
    private int port = -1;
    private PooledByteBufAllocator allocator;

    public TransportServer(TransportContext context, String host, int port) {
        this.context = context;
        this.conf = context.getConf();
        this.allocator = NettyUtils.createPooledByteBufAllocator(conf.preferDirectBuffer(), true, conf.serverThreads());

        boolean close = true;

        try {
            init(host, port);
            close = false;
        } finally {
            if (close) {
                Utils.closeQuietly(this);
            }
        }
    }

    public int getPort() {
        if (port == -1) {
            throw new IllegalStateException("Server not initialized");
        }
        return port;
    }

    private void init(String host, int port) {
        IOMode ioMode = conf.ioMode();
        EventLoopGroup bossGroup = NettyUtils.createEventLoop(ioMode, 1, "boos");
        EventLoopGroup workerGroup = NettyUtils.createEventLoop(ioMode, conf.serverThreads(), "worker");
        bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NettyUtils.getServerChannelClass(ioMode))
                .option(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.SO_REUSEADDR, !SystemUtils.IS_OS_WINDOWS)
                .childOption(ChannelOption.ALLOCATOR, allocator);

        if (conf.backlog() > 0) {
            bootstrap.option(ChannelOption.SO_BACKLOG, conf.backlog());
        }

        if (conf.receiveBuf() > 0) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, conf.receiveBuf());
        }

        if (conf.sendBuf() > 0) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, conf.sendBuf());
        }

        if (conf.enableKeepAlive()) {
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        }

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                log.trace("New connection accepted for remote address {}.", ch.remoteAddress());
                context.initializePipeline(ch);
            }
        });

        InetSocketAddress address = host == null ? new InetSocketAddress(port): new InetSocketAddress(host, port);
        channelFuture = bootstrap.bind(address);
        channelFuture.syncUninterruptibly();

        InetSocketAddress localAddress = (InetSocketAddress) channelFuture.channel().localAddress();
        port = localAddress.getPort();

        log.trace("Server started on {} with port {}", localAddress.getHostString(), port);
    }

    @Override
    public void close() throws IOException {
        if (channelFuture != null) {
            channelFuture.channel().close().awaitUninterruptibly(10, TimeUnit.SECONDS);
            channelFuture = null;
        }
        if (bootstrap != null && bootstrap.config().group() != null) {
            bootstrap.config().group().shutdownGracefully();
        }
        if (bootstrap != null && bootstrap.config().childGroup() != null) {
            bootstrap.config().childGroup().shutdownGracefully();
        }
        bootstrap = null;
    }
}