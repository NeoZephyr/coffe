package storage.hbase.scanner;

import java.io.IOException;

public interface Scanner<T> {
    boolean hasNext() throws IOException;
    T next() throws IOException;
}
