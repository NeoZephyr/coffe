package net.tcp.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.tcp.message.RpcRequestMessage;
import net.tcp.message.RpcResponseMessage;
import net.tcp.service.ProbeService;
import net.tcp.service.ServiceFactory;

import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSeqId(msg.getSeqId());

        try {
            ProbeService service = (ProbeService) ServiceFactory.getService(Class.forName(msg.getFace()));
            Method method = service.getClass().getMethod(msg.getMethod(), msg.getParamTypes());
            Object obj = method.invoke(service, msg.getParamValues());
            response.setValue(obj);
        } catch (Exception e) {
            log.error("exception", e);
            String cause = e.getCause().getMessage();
            response.setException(new Exception("remote process call error: " + cause));
        }

        ctx.writeAndFlush(response);
    }
}