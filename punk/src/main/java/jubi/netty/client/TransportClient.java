package jubi.netty.client;

import io.netty.channel.Channel;
import jubi.netty.handler.TransportResponseProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TransportClient implements Closeable {

    @Getter
    private Channel channel;

    @Getter
    private TransportResponseProcessor processor;
    private volatile boolean timeout;

    private static final AtomicLong counter = new AtomicLong();

    public TransportClient(Channel channel, TransportResponseProcessor processor) {
        this.channel = Objects.requireNonNull(channel);
        this.processor = Objects.requireNonNull(processor);
        this.timeout = false;
    }

    public void timeout() {
        this.timeout = true;
    }

    public boolean isActive() {
        return !timeout && (channel.isOpen() || channel.isActive());
    }

    public SocketAddress getSocketAddress() {
        return channel.remoteAddress();
    }

    public long sendRpc(ByteBuffer message, RpcResponseCallback callback) {
        return requestId();
    }

    @Override
    public void close() throws IOException {
        channel.close().awaitUninterruptibly(10, TimeUnit.SECONDS);
    }

    public static long requestId() {
        return counter.getAndIncrement();
    }
}