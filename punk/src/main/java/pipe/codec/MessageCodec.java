package pipe.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import pipe.message.Message;

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

        // 魔数 4
        buf.writeBytes(new byte[]{1, 2, 3, 4});

        // 版本 1
        buf.writeByte(1);

        // 序列化 1
        buf.writeByte(SerializerAlgorithm.JAVA.ordinal());

        // 消息类型 1
        buf.writeByte(msg.getMsgType());

        // 序列号 4
        buf.writeInt(msg.seqId);

        // 对齐 12 个字节
        buf.writeByte(0xff);

        byte[] bytes = SerializerAlgorithm.JAVA.serialize(msg);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte algorithm = in.readByte();
        byte msgType = in.readByte();
        int seqId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        SerializerAlgorithm serializer = SerializerAlgorithm.values()[algorithm];
        Message msg = serializer.deserialize(Message.getMsgClass(msgType), bytes);

        log.info("magicNum: {}, version: {}, algorithm: {}, message type: {}, seq id: {}, length: {}, message: {}",
                magicNum, version, algorithm, msgType, seqId, length, null);

        out.add(msg);
    }
}