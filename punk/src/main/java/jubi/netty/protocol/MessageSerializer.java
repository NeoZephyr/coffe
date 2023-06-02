package jubi.netty.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface MessageSerializer<T> {

    ByteBuffer serialize(T message) throws IOException;

    T deserialize(ByteBuffer buffer) throws IOException, ClassNotFoundException;
}
