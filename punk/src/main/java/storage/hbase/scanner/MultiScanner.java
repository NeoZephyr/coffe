package storage.hbase.scanner;

import storage.hbase.KeyValue;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MultiScanner implements SeekScanner {

    private static class ScannerNode {
        KeyValue kv;
        SeekScanner scanner;

        public ScannerNode(KeyValue kv, SeekScanner scanner) {
            this.kv = kv;
            this.scanner = scanner;
        }
    }

    private SeekScanner[] scanners;
    private PriorityQueue<ScannerNode> queue;

    public MultiScanner(SeekScanner[] scanners) throws IOException {
        assert scanners != null;
        this.scanners = scanners;
        this.queue = new PriorityQueue<>((Comparator.comparing(o -> o.kv)));

        for (SeekScanner scanner : scanners) {
            if (scanner != null && scanner.hasNext()) {
                queue.add(new ScannerNode(scanner.next(), scanner));
            }
        }
    }

    public MultiScanner(List<SeekScanner> scanners) throws IOException {
        this(scanners.toArray(new SeekScanner[0]));
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public KeyValue next() throws IOException {
        while (!queue.isEmpty()) {
            ScannerNode scannerNode = queue.poll();

            if ((scannerNode.kv == null) || (scannerNode.scanner == null)) {
                continue;
            }

            SeekScanner scanner = scannerNode.scanner;

            if (scanner.hasNext()) {
                queue.add(new ScannerNode(scanner.next(), scanner));
            }

            return scannerNode.kv;
        }

        return null;
    }

    @Override
    public void seekTo(KeyValue kv) throws IOException {
        queue.clear();

        for (SeekScanner scanner : scanners) {
            scanner.seekTo(kv);

            if (scanner.hasNext()) {
                queue.add(new ScannerNode(scanner.next(), scanner));
            }
        }
    }
}
