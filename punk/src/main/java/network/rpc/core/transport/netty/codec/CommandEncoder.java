package network.rpc.core.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import network.rpc.core.transport.command.Command;
import network.rpc.core.transport.command.Header;

public class CommandEncoder extends MessageToByteEncoder<Command> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf out) throws Exception {
        out.writeInt(Integer.BYTES + command.getHeader().length() + command.getPayload().length);
        encodeHeader(ctx, command.getHeader(), out);
        out.writeBytes(command.getPayload());
    }

    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out) {
        out.writeInt(header.getType());
        out.writeInt(header.getVersion());
        out.writeInt(header.getRequestId());
    }
}