package network.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

@Slf4j
public class NativeClient {

    public static void main(String[] args) throws IOException {
        blockService();
        // selectService();
    }

    public static void blockService() throws IOException {
//        try (Socket socket = new Socket("127.0.0.1", 6060)) {
//            OutputStream out = socket.getOutputStream();
//            out.write("hello".getBytes(StandardCharsets.UTF_8));
//            out.write("world".getBytes(StandardCharsets.UTF_8));
//            out.write("中国人民好".getBytes(StandardCharsets.UTF_8));
//        }

        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 6060));

        log.info("local address: {}, remote address: {}", sc.getLocalAddress(), sc.getRemoteAddress());

        sc.write(Charset.defaultCharset().encode("hello\nworld"));
        sc.write(Charset.defaultCharset().encode("0123\n456789abcdefghijklmnopqrstuvwxyz\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcde\n"));

        log.info("waiting...");
        System.in.read();

        sc.close();
    }

    public static void selectService() throws IOException {
        Selector selector = Selector.open();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
        channel.connect(new InetSocketAddress("127.0.0.1", 6060));

        int readCount = 0;

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isConnectable()) {
                    log.info("connect: {}", channel.finishConnect());
                } else if (selectionKey.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                    int n = channel.read(buffer);

                    buffer.clear();
                    log.info("read bytes: {}", n);

                    if (n == -1) {
                        selectionKey.cancel();
                        channel.close();
                        log.info("socket channel close");
                    } else {
                        readCount += n;
                        log.info("read total bytes: {}", readCount);
                    }
                }
            }
        }

    }
}
