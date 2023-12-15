package storage.hbase;

import lombok.extern.slf4j.Slf4j;
import storage.hbase.flusher.Flusher;
import storage.hbase.scanner.MultiScanner;
import storage.hbase.scanner.MapScanner;
import storage.hbase.scanner.SeekScanner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class MemStore {
    private final AtomicLong dataSize = new AtomicLong();
    private volatile ConcurrentSkipListMap<KeyValue, KeyValue> kvMap;
    private volatile ConcurrentSkipListMap<KeyValue, KeyValue> snapshot;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicBoolean flushing = new AtomicBoolean(false);

    private ExecutorService pool;
    private Config config;
    private Flusher flusher;

    public MemStore(Config config, Flusher flusher, ExecutorService pool) {
        this.config = config;
        this.flusher = flusher;
        this.pool = pool;

        this.kvMap = new ConcurrentSkipListMap<>();
        this.snapshot = null;
    }

    public void add(KeyValue kv) throws IOException {
        flushIfNeeded(true);
        lock.readLock().lock();
        try {
            KeyValue prevKeyValue;

            if ((prevKeyValue = kvMap.put(kv, kv)) == null) {
                dataSize.addAndGet(kv.getSize());
            } else {
                dataSize.addAndGet(kv.getSize() - prevKeyValue.getSize());
            }
        } finally {
            lock.readLock().unlock();
        }
        flushIfNeeded(false);
    }

    public long getDataSize() {
        return dataSize.get();
    }

    public boolean isFlushing() {
        return flushing.get();
    }

    private void flushIfNeeded(boolean block) throws IOException {
        if (getDataSize() > config.getMaxMemstoreSize()) {
            if (flushing.get() && block) {
                throw new IOException(
                        "Memstore is full, currentDataSize=" + dataSize.get() + "B, maxMemstoreSize="
                                + config.getMaxMemstoreSize() + "B, please wait until the flushing is finished.");
            } else if (flushing.compareAndSet(false, true)) {
                pool.submit(new FlushTask());
            }
        }
    }

    public SeekScanner createScanner() throws IOException {
        return new MemScanner(kvMap, snapshot);
    }

    private class FlushTask implements Runnable {

        @Override
        public void run() {
            lock.writeLock().lock();

            try {
                snapshot = kvMap;
                kvMap = new ConcurrentSkipListMap<>();
                dataSize.set(0);
            } finally {
                lock.writeLock().unlock();
            }

            boolean success = false;

            for (int i = 0; i < config.getFlushMaxRetries(); i++) {
                try {
                    flusher.flush(new MapScanner(snapshot));
                    success = true;
                } catch (IOException e) {
                    log.error("Failed to flush memstore, retries=" + i + ", maxFlushRetries="
                                    + config.getFlushMaxRetries(), e);

                    if (i >= config.getFlushMaxRetries()) {
                        break;
                    }
                }
            }

            if (success) {
                snapshot = null;
                flushing.compareAndSet(true, false);
            }
        }
    }

    private class MemScanner implements SeekScanner {

        private MultiScanner scanner;

        public MemScanner(NavigableMap<KeyValue, KeyValue> kvSet,
                          NavigableMap<KeyValue, KeyValue> snapshot) throws IOException {
            List<MapScanner> inputs = new ArrayList<>();

            if (kvSet != null && !kvSet.isEmpty()) {
                inputs.add(new MapScanner(kvMap));
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                inputs.add(new MapScanner(snapshot));
            }
            scanner = new MultiScanner(inputs.toArray(new MapScanner[0]));
        }

        @Override
        public boolean hasNext() {
            return scanner.hasNext();
        }

        @Override
        public KeyValue next() throws IOException {
            return scanner.next();
        }

        @Override
        public void seekTo(KeyValue kv) throws IOException {
            scanner.seekTo(kv);
        }
    }
}