package network.rpc.core.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import network.rpc.core.transport.command.Header;

public class RequestEncoder extends CommandEncoder {

    @Override
    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out) {
        super.encodeHeader(ctx, header, out);
    }
}
