package pipe;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Slf4j
public class EchoClient {

    public static final byte[] NEWLINE = {13, 10};

    public static void main(String[] args) throws InterruptedException {
        // testFuture();
        // interactiveChannel();
        testRedis();
    }

    public static void testFuture() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        ChannelFuture future = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception { // exec once
                        log.info("===== init channel");
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("127.0.0.1", 6060);

        // connect 方法是异步的，不等连接建立，就返回了

        Channel channel = future.channel();

        log.info("===== before sync {}", channel);

        future.addListener((ChannelFutureListener) f -> {
            log.info("===== listen channel {}", f.channel());
        });

        future.sync();
        channel = future.channel();

        log.info("===== after sync {}", channel);

        channel.writeAndFlush(date() + ": hello world!");
        channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes("南风一扫胡尘静".getBytes(StandardCharsets.UTF_8)));
        channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes("西入长安到日边".getBytes(StandardCharsets.UTF_8)));

        channel.close();
        ChannelFuture closeFuture = channel.closeFuture();
        // closeFuture.sync();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                group.shutdownGracefully();
                log.info("===== after close");
            }
        });
    }

    public static void interactiveChannel() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception { // exec once
                        log.info("===== init channel");
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("127.0.0.1", 6060)
                .sync() // wait connect complete
                .channel();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();

            if (StringUtils.isBlank(input)) {
                continue;
            }

            input = StringUtils.strip(input);

            if (StringUtils.equalsIgnoreCase(input, "exit")) {
                break;
            }
            channel.writeAndFlush(date() + ": " + input);
        }

        ChannelFuture future = channel.close();
        future.sync();
        group.shutdownGracefully();
    }

    public static void testRedis() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception { // exec once
                        log.info("===== init channel");
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                setValue(ctx, "name", "jack");
                                getValue(ctx, "name");
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.info("=== msg: {}", buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .connect("127.0.0.1", 6379)
                .sync() // wait connect complete
                .channel();

//        ChannelFuture future = channel.close();
//        future.sync();

        channel.closeFuture().sync();
        group.shutdownGracefully();
    }

    private static String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date now = new Date();
        return sdf.format(now);
    }

    private static void getValue(ChannelHandlerContext ctx, String key) {
        String command = "get";
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes("*2".getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(("$" + command.length()).getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(command.getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(("$" + key.length()).getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(key.getBytes());
        buf.writeBytes(NEWLINE);
        ctx.writeAndFlush(buf);
    }

    private static void setValue(ChannelHandlerContext ctx, String key, String value) {
        String command = "set";
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes("*3".getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(("$" + command.length()).getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(command.getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(("$" + key.length()).getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(key.getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(("$" + value.length()).getBytes());
        buf.writeBytes(NEWLINE);
        buf.writeBytes(value.getBytes());
        buf.writeBytes(NEWLINE);
        ctx.writeAndFlush(buf);
    }
}