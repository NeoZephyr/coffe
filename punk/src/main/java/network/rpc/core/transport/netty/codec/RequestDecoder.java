package network.rpc.core.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import network.rpc.core.transport.command.Header;

public class RequestDecoder extends CommandDecoder {

    @Override
    protected Header decodeHeader(ChannelHandlerContext ctx, ByteBuf inBuf) {
        return new Header(
                inBuf.readInt(),
                inBuf.readInt(),
                inBuf.readInt());
    }
}
