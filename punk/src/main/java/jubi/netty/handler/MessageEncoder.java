package jubi.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import jubi.netty.protocol.Message;

@Slf4j
@ChannelHandler.Sharable
public class MessageEncoder extends ChannelOutboundHandlerAdapter {

    public static final MessageEncoder INSTANCE = new MessageEncoder();

    private MessageEncoder() {}

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        // todo: support zero copy
        Message message = (Message) msg;
        int encodeLength = message.encodedLength();
        ByteBuf byteBuf = ctx.alloc().buffer(TransportFrameDecoder.HEADER_SIZE + encodeLength);
        try {
            byteBuf.writeInt(encodeLength);
            byteBuf.writeByte(message.type().id());
            message.encode(byteBuf);
        } catch (Exception e) {
            log.error("Unexpected exception during process encode!", e);
            byteBuf.release();
        }
        ctx.writeAndFlush(byteBuf);
    }
}
