package pipe;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class Chan {
    public static void main(String[] args) throws ParseException {
        fileChan();
        // debugChan();

        // scatter();
        // gather();

        // readSticky();
    }

    public static void readSticky() {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello world\nHello parker\n".getBytes(StandardCharsets.UTF_8));
        split(source);
        source.put("日本安狈\n德国黑狈\n".getBytes(StandardCharsets.UTF_8));
        split(source);
    }

    public static void split(ByteBuffer source) {
        source.flip();
        int limit = source.limit();

        for (int i = 0; i < limit; i++) {
            if (source.get(i) == '\n') {
                log.info("i: {}, position: {}", i, source.position());
                source.limit(i);
                ByteBuffer target = ByteBuffer.allocate(i - source.position());
                target.put(source);
                source.limit(limit);
                source.get();
                ByteBufferUtil.debugAll(target);
                target.flip();
                log.info("target: {}", StandardCharsets.UTF_8.decode(target));
            }
        }

        source.compact();
    }

    public static void debugChan() {
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("你好 abc");
        CharBuffer buffer2 = StandardCharsets.UTF_8.decode(buffer1);

        buffer1.flip();
        ByteBufferUtil.debugRead(buffer1);
        System.out.println(buffer1.toString());
        System.out.println(buffer2.toString());
    }

    public static void scatter() {
        try (RandomAccessFile file = new RandomAccessFile("input/hello.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer a = ByteBuffer.allocate(5);
            ByteBuffer b = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{a, b});
            a.flip();
            b.flip();
            ByteBufferUtil.debugRead(a);
            ByteBufferUtil.debugRead(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void gather() {
        try (RandomAccessFile file = new RandomAccessFile("input/hi.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer d = ByteBuffer.allocate(4);
            ByteBuffer e = ByteBuffer.allocate(4);
            channel.position(1);

            d.put(new byte[]{'f', 'o', 'u', 'r'});
            e.put(new byte[]{'f', 'i', 'v', 'e'});
            d.flip();
            e.flip();
            ByteBufferUtil.debugRead(d);
            ByteBufferUtil.debugRead(e);
            channel.write(new ByteBuffer[]{d, e});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // limit
    // position
    public static void fileChan() {
        try (RandomAccessFile file = new RandomAccessFile("input/hello.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);

            do {
                int len = channel.read(buffer);

                log.info("read bytes: {}", len);

                if (len == -1) {
                    break;
                }

                // 读模式
                buffer.flip();

                while (buffer.hasRemaining()) {
                    char c = (char) buffer.get();
                    log.info("char: {}", c);
                }

                // 写模式
                buffer.clear();

                // buffer.compact();
                // buffer.rewind();
                // buffer.mark();
                // buffer.reset();
            } while (true);

            log.info("size: {}, position: {}", channel.size(), channel.position());
            buffer = ByteBuffer.allocate(5);
            buffer.put("fuck".getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            channel.write(buffer);
            channel.force(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
