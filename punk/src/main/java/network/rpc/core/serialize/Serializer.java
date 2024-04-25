package network.rpc.core.serialize;

public interface Serializer<T> {

    /**
     * 计算对象序列化后的长度，主要用于申请存放序列化数据的字节数组
     */
    int size(T entry);

    /**
     * 序列化对象。将给定的对象序列化成字节数组
     */
    void serialize(T entry, byte[] bytes, int offset, int length);

    /**
     * 反序列化对象
     */
    T parse(byte[] bytes, int offset, int length);

    /**
     * 用一个字节标识对象类型，每种类型的数据应该具有不同的类型值
     */
    byte type();

    /**
     * 返回序列化对象类型的 Class 对象
     */
    Class<T> getEntryClass();
}