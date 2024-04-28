package network.rpc.core.transport.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.rpc.core.transport.RequestHandler;
import network.rpc.core.transport.RequestHandlerRegistry;
import network.rpc.core.transport.command.Command;

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class RequestInvocation extends SimpleChannelInboundHandler<Command> {

    private final RequestHandlerRegistry registry;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command request) throws Exception {
        RequestHandler handler = registry.get(request.getHeader().getType());

        if (handler == null) {
            throw new Exception(String.format("No handler for request with type: %d!", request.getHeader().getType()));
        }

        Command response = handler.handle(request);

        if (response == null) {
            log.warn("Response is null");
            return;
        }

        ctx.writeAndFlush(response).addListener(channelFuture -> {
            if (!channelFuture.isSuccess()) {
                log.warn("Write response failed", channelFuture.cause());
                ctx.channel().close();
            }
        });
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