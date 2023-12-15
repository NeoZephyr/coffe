package storage.hbase.scanner;

import storage.hbase.KeyValue;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedMap;

public class MapScanner implements SeekScanner {

    private SortedMap<KeyValue, KeyValue> sortedMap;
    private Iterator<KeyValue> iterator;

    public MapScanner(SortedMap<KeyValue, KeyValue> sortedMap) {
        this.sortedMap = sortedMap;
        this.iterator = sortedMap.values().iterator();
    }

    @Override
    public boolean hasNext() throws IOException {
        return iterator != null && iterator.hasNext();
    }

    @Override
    public KeyValue next() throws IOException {
        return iterator.next();
    }

    @Override
    public void seekTo(KeyValue kv) throws IOException {
        iterator = sortedMap.tailMap(kv).values().iterator();
    }
}
