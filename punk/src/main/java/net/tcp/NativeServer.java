package net.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NativeServer {
    public static void main(String[] args) throws IOException {
        // selectService();
        multiService();
    }

    public static void blockService() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(6060));
        List<SocketChannel> channels = new ArrayList<>();

        while (true) {
            log.info("connecting...");
            SocketChannel sc = ssc.accept();
            log.info("connected... {}, channel size: {}", sc, channels.size());
            channels.add(sc);

            for (SocketChannel channel : channels) {
                log.info("before read... {}", channel);
                channel.read(buffer);
                buffer.flip();
                ByteBufferUtil.debugRead(buffer);
                buffer.clear();
                log.info("after read...{}", channel);
            }
        }
    }

    public static void selectService() throws IOException {

        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            Selector selector = Selector.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(6060));
            SelectionKey selectionKey = ssc.register(selector, 0, null);
            selectionKey.interestOps(SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                log.info("select count: {}， key count: {}", count, selectionKeys.size());

                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    log.info("key: {}, acceptable: {}, readable: {}, writeable: {}",
                            key, key.isAcceptable(), key.isReadable(), key.isWritable());

                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        Attachment attachment = new Attachment();
                        attachment.readBuffer = ByteBuffer.allocate(10);
                        SelectionKey socketKey = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, attachment);

                        log.info("accept socket channel: {}", socketChannel);

                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < 1000000; i++) {
                            sb.append("a");
                        }
                        ByteBuffer writeBuffer = StandardCharsets.UTF_8.encode(sb.toString());
                        int writeCount = socketChannel.write(writeBuffer);

                        log.info("write bytes: {}", writeCount);

                        if (writeBuffer.hasRemaining()) {
                            socketKey.interestOps(socketKey.interestOps() | SelectionKey.OP_WRITE);
                            attachment = (Attachment) socketKey.attachment();
                            attachment.writeBuffer = writeBuffer;
                            socketKey.attach(attachment);
                        }
                    } else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        Attachment attachment = (Attachment) key.attachment();
                        ByteBuffer buffer = attachment.readBuffer;
                        int n = socketChannel.read(buffer);

                        log.info("read bytes count: {}, position: {}, limit: {}, capacity: {}", n, buffer.position(), buffer.limit(), buffer.capacity());

                        if (n == -1) {
                            key.cancel();
                            socketChannel.close();
                            log.info("socket channel close");
                        } else {
                            split(buffer);

                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                attachment.readBuffer = newBuffer;
                                key.attach(attachment);
                            }
                        }
                    } else if (key.isWritable()) {
                        Attachment attachment = (Attachment) key.attachment();
                        ByteBuffer buffer = attachment.writeBuffer;
                        SocketChannel channel = (SocketChannel) key.channel();
                        int writeCount = channel.write(buffer);

                        log.info("write: {}", writeCount);

                        if (!buffer.hasRemaining()) {
                            // 如果不取消，会每次可写均会触发 write 事件
                            int ops = key.interestOps() ^ SelectionKey.OP_WRITE;

                            if ((ops & SelectionKey.OP_READ) != 0) {
                                log.info("not data to write, remove write");
                                attachment.writeBuffer = null;
                                key.interestOps(ops);
                                key.attach(attachment);
                            } else {
                                log.info("not data to write, close");
                                channel.close();
                                key.cancel();
                            }
                        }
                    }

                    iterator.remove();
                }
            }
        }
    }

    public static void multiService() throws IOException {
        BossEventLoop loop = new BossEventLoop();
        loop.start();
    }

    static class BossEventLoop implements Runnable {

        private Selector selector;
        private WorkerEventLoop[] workers;
        private volatile boolean start = false;
        AtomicInteger idx = new AtomicInteger();

        public void start() throws IOException {
            if (!start) {
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress(6060));
                selector = Selector.open();
                ssc.register(selector, SelectionKey.OP_ACCEPT, null);
                initWorkers();
                new Thread(this, "boss").start();
                start = true;

                log.info("===== start boss server");
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    int count = selector.select();

                    Set<SelectionKey> selectionKeys = selector.selectedKeys();

                    log.info("select count: {}， key count: {}", count, selectionKeys.size());

                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        log.info("key: {}, acceptable: {}, readable: {}, writeable: {}",
                                key, key.isAcceptable(), key.isReadable(), key.isWritable());

                        if (key.isAcceptable()) {
                            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                            SocketChannel socketChannel = channel.accept();
                            socketChannel.configureBlocking(false);
                            int index = (Math.abs(idx.getAndDecrement()) % workers.length);
                            workers[index].register(socketChannel);

                            log.info("accept socket channel: {}", socketChannel.getRemoteAddress());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void initWorkers() {
            this.workers = new WorkerEventLoop[2];

            for (int i = 0; i < workers.length; i++) {
                workers[i] = new WorkerEventLoop(i);
            }
        }
    }

    static class WorkerEventLoop implements Runnable {

        private Selector selector;
        private volatile boolean start = false;
        private int index;
        private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        public WorkerEventLoop(int index) {
            this.index = index;
        }

        public void register(SocketChannel channel) throws IOException {
            if (!start) {
                selector = Selector.open();
                new Thread(this, "worker-" + index).start();
                start = true;
            }

            tasks.add(() -> {
                try {
                    channel.register(selector, SelectionKey.OP_READ, null);
                    int count = selector.selectNow();

                    log.info("exec task, count: {}", count);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = tasks.poll();

                    if (task != null) {
                        task.run();
                    }

                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        log.info("key: {}, acceptable: {}, readable: {}, writeable: {}",
                                key, key.isAcceptable(), key.isReadable(), key.isWritable());

                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);

                            try {
                                int readCount = channel.read(buffer);

                                if (readCount == -1) {
                                    key.cancel();
                                    channel.close();
                                } else {
                                    buffer.flip();
                                    ByteBufferUtil.debugAll(buffer);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                key.cancel();
                                channel.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void split(ByteBuffer source) {
        source.flip();
        int limit = source.limit();

        for (int i = 0; i < limit; i++) {
            if (source.get(i) == '\n') {
                int length = i - source.position();
                ByteBuffer piece = ByteBuffer.allocate(length);

                for (int j = 0; j < length; j++) {
                    piece.put(source.get());
                }

                // drop '\n'
                source.get();
                ByteBufferUtil.debugAll(piece);
            }
        }

        // 为了下一次写
        source.compact();
    }

    static class Attachment {
        ByteBuffer readBuffer;
        ByteBuffer writeBuffer;
    }
}