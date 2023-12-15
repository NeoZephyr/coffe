package storage.hbase;

import storage.hbase.scanner.Scanner;

import java.io.Closeable;
import java.io.IOException;

public interface Table extends Closeable {

//    实现写 WAL 日志功能
//    随着用户的不断写入，WAL 日志可能无限增长。因此，需要控制 WAL 日志的数据量，一旦 flush 操作，之前的 WAL 日志都可以清理
//    在高并发写入的情况下，必须保证 WAL 日志的完整性，需通过排他锁来控制每次写入 WAL 日志的操作，这样会极大地限制吞吐量

//    设计一种 LRU Cache，把经常访问的 Block 缓存在内存中。需要用参数控制 LRU 缓存的总大小

//    DataBlock 的 Seek 操作实现二分查找

//    通过布隆过滤器来优化 Get 操作的性能

    void put(byte[] key, byte[] value) throws IOException;

    KeyValue get(byte[] key);

    void delete(byte[] key) throws IOException;

    Scanner<KeyValue> scan(byte[] startKey, byte[] endKey) throws IOException;

    default Scanner<KeyValue> scan() throws IOException {
        return scan(Bytes.EMPTY, Bytes.EMPTY);
    }
}