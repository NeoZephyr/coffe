package storage.hbase;

import storage.hbase.compactor.Compactor;
import storage.hbase.compactor.DefaultCompactor;
import storage.hbase.flusher.DefaultFlusher;
import storage.hbase.scanner.MultiScanner;
import storage.hbase.scanner.Scanner;
import storage.hbase.scanner.SeekScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Hbase implements Table {

    private ExecutorService pool;
    private MemStore memStore;
    private DiskStore diskStore;
    private Compactor compactor;
    private AtomicLong seqId;
    private Config config;

    public void open() throws IOException {
        this.pool = Executors.newFixedThreadPool(config.getMaxThreadPoolSize());
        this.diskStore = new DiskStore(config.getDataDir(), config.getMaxDiskFiles());
        this.diskStore.open();
        this.seqId = new AtomicLong(0);
        this.memStore = new MemStore(config, new DefaultFlusher(diskStore), pool);
        this.compactor = new DefaultCompactor(diskStore);
        this.compactor.start();
    }

    private Hbase(Config config) {
        this.config = config;
    }

    public static Hbase create(Config config) {
        return new Hbase(config);
    }

    public static Hbase create() {
        return new Hbase(Config.getDefault());
    }

    @Override
    public void put(byte[] key, byte[] value) throws IOException {
        this.memStore.add(KeyValue.put(key, value, seqId.incrementAndGet()));
    }

    @Override
    public KeyValue get(byte[] key) throws IOException {
        KeyValue result = null;
        Scanner<KeyValue> scanner = scan(key, Bytes.EMPTY);

        if (scanner.hasNext()) {
            KeyValue kv = scanner.next();

            if (Bytes.compare(kv.getKey(), key) == 0) {
                result = kv;
            }
        }
        return result;
    }

    @Override
    public void delete(byte[] key) throws IOException {
        this.memStore.add(KeyValue.delete(key, seqId.incrementAndGet()));
    }

    @Override
    public Scanner<KeyValue> scan(byte[] startKey, byte[] endKey) throws IOException {
        List<SeekScanner> scanners = new ArrayList<>();
        scanners.add(memStore.createScanner());
        scanners.add(diskStore.createScanner());
        MultiScanner multiScanner = new MultiScanner(scanners);

        if (Bytes.compare(startKey, Bytes.EMPTY) != 0) {
            multiScanner.seekTo(KeyValue.delete(startKey, seqId.get()));
        }

        KeyValue end = null;

        if (Bytes.compare(endKey, Bytes.EMPTY) != 0) {
            end = KeyValue.delete(endKey, Long.MAX_VALUE);
        }

        return new RangeScanner(end, multiScanner);
    }

    @Override
    public void close() throws IOException {
        // memStore.close();
        diskStore.close();
        compactor.interrupt();
    }

    static class RangeScanner implements Scanner<KeyValue> {

        private KeyValue stopKV;
        private Scanner<KeyValue> storeScanner;
        // Last KV is the last key value which has the largest sequence id in key values with the
        // same key, but diff sequence id or op.
        private KeyValue lastKV = null;
        private KeyValue pendingKV = null;

        public RangeScanner(KeyValue stopKV, SeekScanner it) {
            this.stopKV = stopKV;
            this.storeScanner = it;
        }

        @Override
        public boolean hasNext() throws IOException {
            if (pendingKV == null) {
                switchToNewKey();
            }
            return pendingKV != null;
        }

        private boolean shouldStop(KeyValue kv) {
            return stopKV != null && Bytes.compare(stopKV.getKey(), kv.getKey()) <= 0;
        }

        private void switchToNewKey() throws IOException {
            if (lastKV != null && shouldStop(lastKV)) {
                return;
            }
            KeyValue curKV;
            while (storeScanner.hasNext()) {
                curKV = storeScanner.next();
                if (shouldStop(curKV)) {
                    return;
                }
                if (curKV.getOp() == KeyValue.Op.Put) {
                    if (lastKV == null) {
                        lastKV = pendingKV = curKV;
                        return;
                    }
                    int ret = Bytes.compare(lastKV.getKey(), curKV.getKey());
                    if (ret < 0) {
                        lastKV = pendingKV = curKV;
                        return;
                    } else if (ret > 0) {
                        String msg = "KV mis-encoded, curKV < lastKV, curKV:" + Bytes.toHex(curKV.getKey()) +
                                ", lastKV:" + Bytes.toHex(lastKV.getKey());
                        throw new IOException(msg);
                    }
                    // Same key with lastKV, should continue to fetch the next key value.
                } else if (curKV.getOp() == KeyValue.Op.Delete) {
                    if (lastKV == null || Bytes.compare(lastKV.getKey(), curKV.getKey()) != 0) {
                        lastKV = curKV;
                    }
                } else {
                    throw new IOException("Unknown op code: " + curKV.getOp());
                }
            }
        }

        @Override
        public KeyValue next() throws IOException {
            if (pendingKV == null) {
                switchToNewKey();
            }
            lastKV = pendingKV;
            pendingKV = null;
            return lastKV;
        }
    }
}