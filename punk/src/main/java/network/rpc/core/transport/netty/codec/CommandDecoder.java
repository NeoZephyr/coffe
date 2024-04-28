package network.rpc.core.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import network.rpc.core.transport.command.Command;
import network.rpc.core.transport.command.Header;

import java.util.List;

public abstract class CommandDecoder extends ByteToMessageDecoder {

    private static final int LENGTH_FIELD_LENGTH = Integer.BYTES;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inBuf, List<Object> out) throws Exception {
        if (!inBuf.isReadable(LENGTH_FIELD_LENGTH)) {
            return;
        }

        inBuf.markReaderIndex();
        int len = inBuf.readInt() - LENGTH_FIELD_LENGTH;

        if (inBuf.readableBytes() < len) {
            inBuf.resetReaderIndex();
            return;
        }

        Header header = decodeHeader(ctx, inBuf);
        int payloadLen = len - header.length();
        byte[] payload = new byte[payloadLen];
        inBuf.readBytes(payload);
        out.add(new Command(header, payload));
    }

    protected abstract Header decodeHeader(ChannelHandlerContext ctx, ByteBuf inBuf) ;
}