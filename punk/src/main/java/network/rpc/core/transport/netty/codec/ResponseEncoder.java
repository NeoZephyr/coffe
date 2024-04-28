package network.rpc.core.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import network.rpc.core.transport.command.Header;
import network.rpc.core.transport.command.ResponseHeader;

import java.nio.charset.StandardCharsets;

public class ResponseEncoder extends CommandEncoder {

    @Override
    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out) {
        super.encodeHeader(ctx, header, out);

        ResponseHeader responseHeader = (ResponseHeader) header;
        out.writeInt(responseHeader.getCode());
        int errorLen = header.length() - (Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES);
        out.writeInt(errorLen);
        out.writeBytes(responseHeader.getError() == null ? new byte[0] : responseHeader.getError().getBytes(StandardCharsets.UTF_8));
    }
}
