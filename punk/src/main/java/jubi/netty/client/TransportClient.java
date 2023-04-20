package jubi.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import jubi.netty.exception.RpcException;
import jubi.netty.handler.TransportResponseHandler;
import jubi.netty.protocol.Message;
import jubi.netty.util.NettyUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TransportClient implements Closeable {

    private Channel channel;
    private TransportResponseHandler handler;
    private volatile boolean timeout;

    private static final AtomicLong counter = new AtomicLong();

    public TransportClient(Channel channel, TransportResponseHandler handler) {
        this.channel = Objects.requireNonNull(channel);
        this.handler = Objects.requireNonNull(handler);
        this.timeout = false;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isActive() {
        return !timeout && (channel.isOpen() || channel.isActive());
    }

    public SocketAddress getSocketAddress() {
        return channel.remoteAddress();
    }

    public ChannelFuture sendData(Message message, RpcResponseCallback callback) {
        long requestId = requestId();
        handler.addCallback(requestId, callback);
        RpcChannelListener listener = new RpcChannelListener(requestId, callback);
        return channel.writeAndFlush(message).addListener(listener);
    }

    @Override
    public void close() throws IOException {
        channel.close().awaitUninterruptibly(10, TimeUnit.SECONDS);
    }

    public static long requestId() {
        return counter.getAndIncrement();
    }

    public class StdChannelListener implements GenericFutureListener<Future<? super Void>> {
        long startTime;
        long requestId;

        public StdChannelListener(long requestId) {
            this.startTime = System.currentTimeMillis();
            this.requestId = requestId;
        }

        @Override
        public void operationComplete(Future<? super Void> future) throws Exception {
            if (future.isSuccess()) {
                long cost = System.currentTimeMillis() - startTime;
                log.trace("Sending request {} to {} took {} ms", requestId, NettyUtils.getRemoteAddress(channel), cost);
            } else {
                String errorMsg = String.format("Failed to send request %s to %s: %s, channel will be closed",
                        requestId, NettyUtils.getRemoteAddress(channel), future.cause());
                log.warn(errorMsg);
                channel.close();

                try {
                    handleFailure(errorMsg, future.cause());
                } catch (Exception e) {
                    log.error("Uncaught exception in RPC response callback handler!", e);
                }
            }
        }

        protected void handleFailure(String errorMsg, Throwable cause) {
            log.error("error encountered {}", errorMsg, cause);
        }
    }

    private class RpcChannelListener extends StdChannelListener {

        RpcResponseCallback callback;

        public RpcChannelListener(long requestId, RpcResponseCallback callback) {
            super(requestId);
            this.callback = callback;
        }

        @Override
        protected void handleFailure(String errorMsg, Throwable cause) {
            handler.removeCallback(requestId);
            callback.onFailure(new RpcException(errorMsg, cause));
        }
    }
}