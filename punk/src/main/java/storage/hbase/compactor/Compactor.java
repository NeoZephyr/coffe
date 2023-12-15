package storage.hbase.compactor;

import java.io.IOException;

public abstract class Compactor extends Thread {
    public abstract void compact() throws IOException;
}