package pipe.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import pipe.bean.Message;

import java.util.List;

public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf buf) throws Exception {
        buf.writeBytes(new byte[]{1, 0, 1, 7});
        buf.writeByte(1);

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        int magicNum = buf.readInt();
    }
}