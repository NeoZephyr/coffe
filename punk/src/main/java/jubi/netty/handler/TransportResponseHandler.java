package jubi.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import jubi.netty.client.RpcResponseCallback;
import jubi.netty.exception.RpcException;
import jubi.netty.protocol.RpcResponse;
import jubi.netty.util.NettyUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TransportResponseHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;
    private Map<Long, RpcResponseCallback> callbacks;

    public TransportResponseHandler(Channel channel) {
        this.channel = channel;
        this.callbacks = new ConcurrentHashMap<>();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            RpcResponse response = (RpcResponse) msg;
            RpcResponseCallback callback = callbacks.get(response.getRequestId());

            if (callback == null) {
                log.warn("Ignoring response from {} since it is not exist", NettyUtils.getRemoteAddress(channel));
            } else {
                callback.onSuccess(response);
            }
        } else {
            throw new RpcException("receive unexpected message!");
        }
        super.channelRead(ctx, msg);
    }

    public void addCallback(long requestId, RpcResponseCallback callback) {
        callbacks.put(requestId, callback);
    }

    public void removeCallback(long requestId) {
        callbacks.remove(requestId);
    }
}