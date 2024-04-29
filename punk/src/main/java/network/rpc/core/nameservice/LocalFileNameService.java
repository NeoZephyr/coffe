package network.rpc.core.nameservice;

import lombok.extern.slf4j.Slf4j;
import network.rpc.api.NameService;
import network.rpc.core.serialize.SerializeSupport;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class LocalFileNameService implements NameService {

    private static final Collection<String> protocols = Collections.singleton("file");
    private File file;

    @Override
    public Collection<String> supportedProtocols() {
        return protocols;
    }

    @Override
    public void connect(URI uri) {
        if (protocols.contains(uri.getScheme())) {
            file = new File(uri);
        } else {
            throw new RuntimeException("Unsupported schema");
        }
    }

    @Override
    public synchronized void registerService(String serviceName, URI uri) throws IOException {
        log.info("Register service: {}, uri: {}", serviceName, uri);

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = raf.getChannel()) {
            FileLock lock = fileChannel.lock();

            try {
                int fileLen = (int) raf.length();
                Metadata metadata;
                byte[] bytes;

                if (fileLen > 0) {
                    bytes = new byte[fileLen];
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);

                    while (buffer.hasRemaining()) {
                        fileChannel.read(buffer);
                    }

                    metadata = SerializeSupport.parse(bytes);
                } else {
                    metadata = new Metadata();
                }

                List<URI> uris = metadata.computeIfAbsent(serviceName, k -> new ArrayList<>());

                if (!uris.contains(uri)) {
                    uris.add(uri);
                }

                bytes = SerializeSupport.serialize(metadata);
                fileChannel.truncate(bytes.length);
                fileChannel.position(0L);
                fileChannel.write(ByteBuffer.wrap(bytes));
                fileChannel.force(true);
            } finally {
                lock.release();
            }
        }
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        Metadata metadata;

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = raf.getChannel()) {
            FileLock lock = fileChannel.lock();

            try {
                byte[] bytes = new byte[(int) raf.length()];
                ByteBuffer buffer = ByteBuffer.wrap(bytes);

                while (buffer.hasRemaining()) {
                    fileChannel.read(buffer);
                }

                metadata = bytes.length == 0 ? new Metadata() : SerializeSupport.parse(bytes);
            } finally {
                lock.release();
            }
        }

        List<URI> uris = metadata.get(serviceName);

        if (null == uris || uris.isEmpty()) {
            return null;
        } else {
            return uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
        }
    }
}