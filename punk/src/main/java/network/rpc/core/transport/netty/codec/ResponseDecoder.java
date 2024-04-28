package network.rpc.core.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import network.rpc.core.transport.command.Header;
import network.rpc.core.transport.command.ResponseHeader;

import java.nio.charset.StandardCharsets;

public class ResponseDecoder extends CommandDecoder {

    @Override
    protected Header decodeHeader(ChannelHandlerContext ctx, ByteBuf inBuf) {
        int type = inBuf.readInt();
        int version = inBuf.readInt();
        int requestId = inBuf.readInt();
        int code = inBuf.readInt();
        int errorLen = inBuf.readInt();
        byte[] errorBytes = new byte[errorLen];
        inBuf.readBytes(errorBytes);
        String error = new String(errorBytes, StandardCharsets.UTF_8);

        return new ResponseHeader(type, version, requestId, code, error);
    }
}