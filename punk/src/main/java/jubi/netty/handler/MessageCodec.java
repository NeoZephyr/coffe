package jubi.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import jubi.netty.protocol.Message;
import jubi.netty.protocol.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * ByteToMessageCodec 子类不能标记为 Sharable
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        msg.type().encode(buf);
        msg.encode(buf);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Message.Type msgType = Message.Type.decode(in);
        Message message = decode(msgType, in);
        out.add(message);
    }

    private Message decode(Message.Type msgType, ByteBuf in) {
        switch (msgType) {
            case RPC_REQUEST:
                return RpcRequest.decode(in);
            default:
                throw new IllegalArgumentException("Unexpected message type: " + msgType);
        }
    }
}