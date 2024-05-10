package network.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Slf4j
public class HttpServer {

    public static void main(String[] args) throws InterruptedException {
        start();
    }

    public static void start0() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpServerExpectContinueHandler());
                            p.addLast(new HttpHelloWorldServerHandler());
                        }
                    });

            Channel ch = b.bind(8080).sync().channel();

            System.err.println("Open your web browser and navigate to " +
                    "http://127.0.0.1:8080");

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void start() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup(2);

        // NioEventLoopGroup -> thread pool + selector
        new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception { // exec once
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new HttpServerCodec());

                        // 写大数据流
                        // ch.pipeline().addLast(new ChunkedWriteHandler());
                        // 对 http message 进行聚合，形成 FullHttpRequest 或者 FullHttpResponse
                        // ch.pipeline().addLast(new HttpObjectAggregator(1024 * 64));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof HttpRequest) {
                                    log.info("=== request header");
                                } else if (msg instanceof HttpContent) {
                                    log.info("=== request content");
                                }

                                ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {
                                log.info("=== url: {}", request.uri());

                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), OK);
                                byte[] bytes = "<h1>Hello, world!</h1>".getBytes();
                                response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                response.content().writeBytes(bytes);
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                })
                .bind(6060)
                .sync();
    }

    static class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<HttpObject> {

        private static final byte[] CONTENT = "helloworld".getBytes();

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;

                FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), OK,
                        Unpooled.wrappedBuffer(CONTENT));
                response.headers()
                        .set(CONTENT_TYPE, TEXT_PLAIN)
                        .setInt(CONTENT_LENGTH, response.content().readableBytes());

                ChannelFuture f = ctx.write(response);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

}
