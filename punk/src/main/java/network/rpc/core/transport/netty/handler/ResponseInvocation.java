package network.rpc.core.transport.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.rpc.core.transport.InFlightRequests;
import network.rpc.core.transport.ResponseFuture;
import network.rpc.core.transport.command.Command;

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class ResponseInvocation extends SimpleChannelInboundHandler<Command> {

    private final InFlightRequests inFlightRequests;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command response) throws Exception {
        ResponseFuture future = inFlightRequests.remove(response.getHeader().getRequestId());

        if (future != null) {
            future.getFuture().complete(response);
        } else {
            log.warn("Drop response: {}", response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Exception: ", cause);

        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();

        if (channel.isActive()) {
            ctx.close();
        }
    }
}