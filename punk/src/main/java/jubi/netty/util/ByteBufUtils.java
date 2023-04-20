package jubi.netty.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class ByteBufUtils {

    public static int encodedLength(String s) {
        return 4 + s.getBytes(StandardCharsets.UTF_8).length;
    }

    public static int encodedLength(ByteBuf buf) {
        return 4 + buf.readableBytes();
    }

    public static void writeLengthAndString(ByteBuf buf, String str) {
        if (str == null) {
            buf.writeInt(-1);
            return;
        }

        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static String readLengthAndString(ByteBuf buf) {
        int length = buf.readInt();

        if (length == -1) {
            return null;
        }

        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
