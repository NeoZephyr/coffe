package storage.hbase.flusher;

import storage.hbase.KeyValue;
import storage.hbase.scanner.Scanner;

import java.io.IOException;

public interface Flusher {
    void flush(Scanner<KeyValue> set) throws IOException;
}
