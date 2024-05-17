package network.rpc.core.transport.netty;

import common.NettyUtils;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import network.rpc.core.transport.InFlightRequests;
import network.rpc.core.transport.ResponseFuture;
import network.rpc.core.transport.Transport;
import network.rpc.core.transport.command.Command;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class NettyTransport implements Transport {

    private final Channel channel;
    private final InFlightRequests inFlightRequests;

    NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = Objects.requireNonNull(channel);
        this.inFlightRequests = Objects.requireNonNull(inFlightRequests);
    }

    public boolean isActive() {
        return channel.isOpen() || channel.isActive();
    }

    @Override
    public SocketAddress getAddress() {
        return channel.remoteAddress();
    }

    @Override
    public CompletableFuture<Command> send(Command request) {
        CompletableFuture<Command> future = new CompletableFuture<>();

        try {
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(), future));
            channel.writeAndFlush(request)
                    .addListener(new ChannelListener(request.getHeader().getRequestId()));
        } catch (Exception e) {
            inFlightRequests.remove(request.getHeader().getRequestId());
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    public void close() throws IOException {
        channel.close();
        // channel.close().awaitUninterruptibly(10, TimeUnit.SECONDS);
    }

    /**
     * 发送完成监听
     *
     * implements GenericFutureListener<ChannelFuture>
     */
    private class ChannelListener implements GenericFutureListener<Future<? super Void>> {

        long startTime;
        int requestId;

        public ChannelListener(int requestId) {
            this.requestId = requestId;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void operationComplete(Future<? super Void> future) throws Exception {
            long cost = System.currentTimeMillis() - startTime;

            if (future.isSuccess()) {
                log.trace("Send request {} to {} success, took {}ms",
                        requestId, NettyUtils.getRemoteAddress(channel), cost);
            } else {
                log.warn("Send request {} to {} failed, took {}ms",
                        requestId, NettyUtils.getRemoteAddress(channel), cost, future.cause());

                ResponseFuture responseFuture = inFlightRequests.remove(requestId);

                if (responseFuture != null) {
                    responseFuture.getFuture().completeExceptionally(future.cause());
                }

                // fire exception on failure
                // channel.pipeline().fireExceptionCaught(future.cause());
                channel.close();
            }
        }
    }
}
