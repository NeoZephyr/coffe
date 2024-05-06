package network.tcp.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import network.tcp.codec.MessageCodec;
import network.tcp.codec.ProcotolFrameDecoder;
import network.tcp.handler.RpcResponseMessageHandler;
import network.tcp.message.RpcRequestMessage;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ServiceFactory {

    public static <T> T getService(Class<T> face) {
        if (face == ProbeService.class) {
            return (T) new DefaultProbeService();
        }

        return null;
    }

    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private static volatile Channel channel = null;
    private static final Object lock = new Object();

    public static <T> T getProxyService(Class<T> face) {
        ClassLoader classLoader = face.getClassLoader();
        Class<?>[] faces = new Class[]{face};
        Object proxyInstance = Proxy.newProxyInstance(classLoader, faces, (proxy, method, args) -> {
            int seqId = idGenerator.getAndIncrement();
            RpcRequestMessage msg = new RpcRequestMessage(
                    seqId,
                    face.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            getChannel().writeAndFlush(msg).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("send request success");
                } else {
                    log.warn("send request error");
                }
            });

            // Promise 来接收结果
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.promises.put(seqId, promise);

            // promise.addListener(future -> {});
            promise.await(1, TimeUnit.SECONDS);

            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        });

        return (T) proxyInstance;
    }

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }

        synchronized (lock) {
            if (channel != null) {
                return channel;
            }

            initChannel();

            return channel;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec messageCodec = new MessageCodec();
        RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();

        try {
            channel = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception { // exec once
                            log.info("===== init channel");
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodec);
                            ch.pipeline().addLast(rpcHandler);
                        }
                    })
                    .connect("127.0.0.1", 6060)
                    .sync() // wait connect complete
                    .channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("init channel error", e);
        }
    }
}
