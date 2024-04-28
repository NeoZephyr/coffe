package network.rpc.core.transport.netty;

import io.netty.channel.Channel;
import network.rpc.core.transport.InFlightRequests;
import network.rpc.core.transport.ResponseFuture;
import network.rpc.core.transport.Transport;
import network.rpc.core.transport.command.Command;

import java.util.concurrent.CompletableFuture;

public class NettyTransport implements Transport {

    private final Channel channel;
    private final InFlightRequests inFlightRequests;

    NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = channel;
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    public CompletableFuture<Command> send(Command request) {
        CompletableFuture<Command> future = new CompletableFuture<>();

        try {
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(), future));
            channel.writeAndFlush(request).addListener(channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    future.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Exception e) {
            inFlightRequests.remove(request.getHeader().getRequestId());
            future.completeExceptionally(e);
        }

        return future;
    }
}
