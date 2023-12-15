package storage.hbase.scanner;

import storage.hbase.KeyValue;

import java.io.IOException;

public interface SeekScanner extends Scanner<KeyValue> {

    void seekTo(KeyValue kv) throws IOException;
}