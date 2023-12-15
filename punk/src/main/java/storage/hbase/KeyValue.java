package storage.hbase;

import java.io.IOException;
import java.util.Comparator;

public class KeyValue implements Comparable<KeyValue> {

    public static final int OP_SIZE = 1;
    public static final int SEQ_ID_SIZE = 8;
    public static final int KEY_LEN_SIZE = 4;
    public static final int VALUE_LEN_SIZE = 4;

    public static final KeyValueComparator KV_CMP = new KeyValueComparator();

    private byte[] key;
    private byte[] value;
    private long seqId;
    private Op op;

    public enum Op {
        Put((byte) 0),
        Delete((byte) 1);

        private byte code;

        Op(byte code) {
            this.code = code;
        }

        public static Op fromCode(byte code) {
            switch (code) {
                case 0:
                    return Put;
                case 1:
                    return Delete;
                default:
                    throw new IllegalArgumentException("Unknown code: " + code);
            }
        }

        public byte getCode() {
            return code;
        }
    }

    public static KeyValue create(byte[] key, byte[] value, Op op, long sequenceId) {
        return new KeyValue(key, value, op, sequenceId);
    }

    public static KeyValue put(byte[] key, byte[] value, long sequenceId) {
        return create(key, value, Op.Put, sequenceId);
    }

    public static KeyValue delete(byte[] key, long sequenceId) {
        return create(key, Bytes.EMPTY, Op.Delete, sequenceId);
    }

    private KeyValue(byte[] key, byte[] value, Op op, long seqId) {
        this.key = key;
        this.value = value;
        this.op = op;
        this.seqId = seqId;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public long getSeqId() {
        return seqId;
    }

    public Op getOp() {
        return op;
    }

    private int geRawtKeyLen() {
        return key.length + OP_SIZE + SEQ_ID_SIZE;
    }

    public int getSize() {
        return KEY_LEN_SIZE + VALUE_LEN_SIZE + geRawtKeyLen() + value.length;
    }

    public byte[] toBytes() {
        int rawKeyLen = geRawtKeyLen();
        int pos = 0;
        byte[] data = new byte[getSize()];

        byte[] rawKeyLenBytes = Bytes.toBytes(rawKeyLen);
        System.arraycopy(rawKeyLenBytes, 0, data, pos, KEY_LEN_SIZE);
        pos += KEY_LEN_SIZE;

        byte[] valueLenBytes = Bytes.toBytes(value.length);
        System.arraycopy(valueLenBytes, 0, data, pos, VALUE_LEN_SIZE);
        pos += VALUE_LEN_SIZE;

        System.arraycopy(key, 0, data, pos, key.length);
        pos += key.length;

        data[pos] = op.getCode();
        pos += OP_SIZE;

        byte[] seqIdBytes = Bytes.toBytes(seqId);
        System.arraycopy(seqIdBytes, 0, data, pos, seqIdBytes.length);
        pos += SEQ_ID_SIZE;

        System.arraycopy(value, 0, data, pos, value.length);
        return data;
    }

    public static KeyValue fromBytes(byte[] data) throws IOException {
        return fromBytes(data, 0);
    }

    public static KeyValue fromBytes(byte[] data, int offset) throws IOException {
        if (data == null) {
            throw new IOException("Buffer is null");
        }

        if (offset + KEY_LEN_SIZE + VALUE_LEN_SIZE >= data.length) {
            throw new IOException("Invalid offset or len. offset: " + offset + ", len: " + data.length);
        }

        int pos = offset;
        int rawKeyLen = Bytes.toInt(Bytes.slice(data, pos, KEY_LEN_SIZE));
        pos += KEY_LEN_SIZE;

        int valueLen = Bytes.toInt(Bytes.slice(data, pos, VALUE_LEN_SIZE));
        pos += VALUE_LEN_SIZE;

        int keyLen = rawKeyLen - OP_SIZE - SEQ_ID_SIZE;
        byte[] key = Bytes.slice(data, pos, keyLen);
        pos += keyLen;

        Op op = Op.fromCode(data[pos]);
        pos += 1;

        long seqId = Bytes.toLong(Bytes.slice(data, pos, SEQ_ID_SIZE));
        pos += SEQ_ID_SIZE;

        byte[] value = Bytes.slice(data, pos, valueLen);
        return create(key, value, op, seqId);
    }

    private static class KeyValueComparator implements Comparator<KeyValue> {
        @Override
        public int compare(KeyValue a, KeyValue b) {
            if (a == b) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return a.compareTo(b);
        }
    }

    @Override
    public int compareTo(KeyValue kv) {
        if (kv == null) {
            throw new IllegalArgumentException("kv to compare should not be null");
        }

        int ret = Bytes.compare(this.key, kv.key);

        if (ret != 0) {
            return ret;
        }

        if (this.seqId != kv.seqId) {
            return this.seqId > kv.seqId ? -1 : 1;
        }

        if (this.op != kv.op) {
            return this.op.getCode() > kv.op.getCode() ? -1 : 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object kv) {
        if (kv == null) return false;
        if (!(kv instanceof KeyValue)) return false;
        KeyValue that = (KeyValue) kv;
        return this.compareTo(that) == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("key=").append(Bytes.toHex(this.key))
                .append("/op=").append(op)
                .append("/seqId=").append(this.seqId)
                .append("/value=").append(Bytes.toHex(this.value));
        return sb.toString();
    }
}