package jubi.netty.client;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import jubi.netty.handler.TransportResponseProcessor;
import jubi.netty.protocol.RpcRequest;
import jubi.netty.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import jubi.netty.exception.RpcException;
import jubi.netty.util.NettyUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TransportClient implements Closeable {

    private Channel channel;
    private TransportResponseProcessor processor;
    private volatile boolean timeout;

    private static final AtomicLong counter = new AtomicLong();

    public TransportClient(Channel channel, TransportResponseProcessor processor) {
        this.channel = Objects.requireNonNull(channel);
        this.processor = Objects.requireNonNull(processor);
        this.timeout = false;
    }

    public Channel getChannel() {
        return channel;
    }

    public void timeout() {
        this.timeout = true;
    }

    public TransportResponseProcessor getProcessor() {
        return processor;
    }

    public boolean isActive() {
        return !timeout && (channel.isOpen() || channel.isActive());
    }

    public SocketAddress getSocketAddress() {
        return channel.remoteAddress();
    }

    public long sendRpc(ByteBuffer message, RpcResponseCallback callback) {
        long requestId = requestId();
        processor.addRpcCallback(requestId, callback);
        RpcChannelListener listener = new RpcChannelListener(requestId, callback);
        channel.writeAndFlush(new RpcRequest(requestId, message)).addListener(listener);
        return requestId;
    }

    public String sendRpcSync(ByteBuffer message, long timeoutMs) throws InterruptedException, ExecutionException, TimeoutException {
        SettableFuture<String> result = SettableFuture.create();

        sendRpc(message, new RpcResponseCallback() {
            @Override
            public void onSuccess(RpcResponse response) {
                try {
                    result.set(response.getMessage());
                } catch (Throwable t) {
                    log.warn("Error in responding RPC callback", t);
                    result.setException(t);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                result.setException(e);
            }
        });

        return result.get(timeoutMs, TimeUnit.MILLISECONDS);
    }

    // fetchChunk and stream

    public void removeRpcCallback(long requestId) {
        processor.removeRpcCallback(requestId);
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
                log.warn(errorMsg, future.cause());
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
            processor.removeRpcCallback(requestId);
            callback.onFailure(new RpcException(errorMsg, cause));
        }
    }
}