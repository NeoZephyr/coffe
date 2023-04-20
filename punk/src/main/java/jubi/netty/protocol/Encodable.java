package jubi.netty.protocol;

import io.netty.buffer.ByteBuf;

public interface Encodable {

    /** Number of bytes of the encoded form of this object. */
    int encodedLength();

    void encode(ByteBuf buf);
}