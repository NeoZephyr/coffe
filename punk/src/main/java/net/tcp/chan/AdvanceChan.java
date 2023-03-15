package net.tcp.chan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

@Slf4j
public class AdvanceChan {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);

        // ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer(10);
        // ByteBuf buffer = ByteBufAllocator.DEFAULT.directBuffer(10);

        log.info("{}, {}", buffer.getClass(), buffer.maxCapacity());
        dumpBuf(buffer);

        // 024a
        buffer.writeInt(586);     // Big Endian(network)      024a
        buffer.writeIntLE(586);   // Little Endian            4a02
        buffer.writeBytes(new byte[]{5, 6, 7, 8, 9, 10, 11, 12, 13});
        dumpBuf(buffer);

        buffer.markReaderIndex();
        System.out.println(buffer.readInt());
        dumpBuf(buffer);
        buffer.resetReaderIndex();
        System.out.println(buffer.readInt());
        dumpBuf(buffer);
        System.out.println(buffer.readInt());
        dumpBuf(buffer);
        System.out.println(buffer.readInt());
        dumpBuf(buffer);

        log.info("===== slice");

        ByteBuf slice = buffer.slice(buffer.readerIndex(), 3);
        dumpBuf(slice);

        slice.setByte(0, 14);
        dumpBuf(slice);
        dumpBuf(buffer);

        // 当包装 ByteBuf 个数超过一个时, 底层使用了 CompositeByteBuf
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(6);
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(8);
        buf1.writeBytes(new byte[]{1, 2, 3, 4});
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});
        ByteBuf buf3 = Unpooled.wrappedBuffer(buf1, buf2);

        log.info("===== Unpooled buffer");
        dumpBuf(buf3);

        buffer.release();
    }

    private static void dumpBuf(ByteBuf buffer) {
        StringBuilder buf = new StringBuilder()
                .append("read index:").append(buffer.readerIndex())
                .append(", write index:").append(buffer.writerIndex())
                .append(", capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);

        log.info(buf.toString());
    }
}
