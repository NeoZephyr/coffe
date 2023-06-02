package jubi.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jubi.netty.client.TransportClient;
import jubi.netty.protocol.Message;
import jubi.netty.protocol.RequestMessage;
import jubi.netty.protocol.ResponseMessage;
import jubi.netty.util.NettyUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransportChannelHandler extends SimpleChannelInboundHandler<Message> {
    private final TransportClient client;
    private final TransportRequestProcessor requestProcessor;
    private final TransportResponseProcessor responseProcessor;
    private final long requestTimeoutMs;
    private final boolean closeIdleConnections;

    public TransportChannelHandler(TransportClient client,
                                   TransportRequestProcessor requestProcessor,
                                   TransportResponseProcessor responseProcessor,
                                   long requestTimeoutMs,
                                   boolean closeIdleConnections) {
        this.client = client;
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
        this.requestTimeoutMs = requestTimeoutMs;
        this.closeIdleConnections = closeIdleConnections;
    }

    public TransportClient getClient() {
        return client;
    }

    public TransportResponseProcessor getResponseProcessor() {
        return responseProcessor;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message request) throws Exception {
        if (request instanceof RequestMessage) {
            requestProcessor.handle((RequestMessage) request);
        } else if (request instanceof ResponseMessage) {
            responseProcessor.handle((ResponseMessage) request);
        } else {
            ctx.fireChannelRead(request);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            // avoid a race
            synchronized (this) {
                boolean isActuallyOverdue = System.currentTimeMillis() - responseProcessor.getLastRequestTime() > requestTimeoutMs;

                if (e.state() == IdleState.ALL_IDLE && isActuallyOverdue) {
                    if (responseProcessor.hasOutstandingRequests()) {
                        String address = NettyUtils.getRemoteAddress(ctx.channel());
                        log.error("Connection to {} has been quiet for {} ms while there are outstanding requests. Assuming connection is dead.",
                                address, requestTimeoutMs);
                        client.timeout();
                        ctx.close();
                    } else if (closeIdleConnections) {
                        client.timeout();
                        ctx.close();
                    }
                }
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            requestProcessor.channelActive();
        } catch (RuntimeException e) {
            log.error("Exception from request handler while channel is active", e);
        }
        try {
            responseProcessor.channelActive();
        } catch (RuntimeException e) {
            log.error("Exception from response handler while channel is active", e);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            requestProcessor.channelInactive();
        } catch (RuntimeException e) {
            log.error("Exception from request handler while channel is inactive", e);
        }
        try {
            responseProcessor.channelInactive();
        } catch (RuntimeException e) {
            log.error("Exception from response handler while channel is inactive", e);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Exception in connection from " + NettyUtils.getRemoteAddress(ctx.channel()), cause);

        requestProcessor.exceptionCaught(cause);
        responseProcessor.exceptionCaught(cause);
        ctx.close();
    }
}