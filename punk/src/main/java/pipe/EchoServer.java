package pipe;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;
import pipe.codec.MessageCodec;
import pipe.codec.ProcotolFrameDecoder;
import pipe.handler.RpcRequestMessageHandler;

@Slf4j
public class EchoServer {

    public static void main(String[] args) throws InterruptedException {
        // loopGroup();
        // runServer();
        // handlerFlow();
    }

    public static void loopGroup() {
        DefaultEventLoopGroup group = new DefaultEventLoopGroup(2);
        log.info("loop: {}", group.next());
        log.info("loop: {}", group.next());
        log.info("loop: {}", group.next());

        for (EventExecutor loop : group) {
            log.info("for loop: {}", loop);
        }

        group.shutdownGracefully();
    }

    public static void runServer() throws InterruptedException {
        DefaultEventLoopGroup ioWorkers = new DefaultEventLoopGroup(2);

        // NioEventLoopGroup -> thread pool + selector
        new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception { // exec once
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

                        // AbstractChannelHandlerContext#invokeChannelRead
                        // 指定 io 线程
                        ch.pipeline().addLast(ioWorkers, "io", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = msg instanceof ByteBuf ? ((ByteBuf) msg) : null;

                                if (buf != null) {
                                    log.info("first handler, buf size: {}", buf.readableBytes());
                                    byte[] bytes = new byte[256];
                                    buf.readBytes(bytes, 0, buf.readableBytes());
                                    // buf.readBytes(bytes, 0, 10);
                                    log.info("first handler, buf: {}", new String(bytes));
                                }

                                ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                log.info("third handler, msg: {}", msg);
                            }
                        });
                    }
                })
                .bind(6060)
                .sync();
    }

    // in1 -> in2 -> out1 -> out2
    public static void handleFlow() throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(3))
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_RCVBUF, 10) // 接收缓冲区
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new IdleStateHandler(10, 0, 0));
                        ch.pipeline().addLast(new ChannelDuplexHandler() {
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                IdleStateEvent event = (IdleStateEvent) evt;

                                if (event.state() == IdleState.READER_IDLE) {
                                    log.info("=== read idle timeout");
                                    ctx.channel().close();
                                }
                            }
                        });

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("===== input1");

                                // 调用下一个入站处理器
                                super.channelRead(ctx, msg);

                                // ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("===== input2");
                                super.channelRead(ctx, msg);

                                // 从尾部开始触发后续出站处理器的执行
                                ctx.channel().write(msg);
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("===== output1");

                                // 触发上一个出站处理器
                                ctx.write(msg, promise);
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("===== output2");
                                ctx.write(msg, promise);
                            }
                        });
                    }
                })
                .bind(6060)
                .sync();

        log.info("===== start");
    }

    public static void handleRpc() {
        NioEventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup worker = new NioEventLoopGroup(4);

        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec messageCodec = new MessageCodec();
        RpcRequestMessageHandler rpcHandler = new RpcRequestMessageHandler();

        try {
            Channel channel = new ServerBootstrap().channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodec);
                            ch.pipeline().addLast(rpcHandler);
                        }
                    })
                    .bind(6060)
                    .sync()
                    .channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}