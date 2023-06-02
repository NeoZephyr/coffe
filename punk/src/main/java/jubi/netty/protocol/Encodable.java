package jubi.netty.protocol;

import io.netty.buffer.ByteBuf;

public interface Encodable {

    void encode(ByteBuf buf);
}