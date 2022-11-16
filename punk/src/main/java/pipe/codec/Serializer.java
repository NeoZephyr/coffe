package pipe.codec;

public interface Serializer {

    <T> T deserialize(Class<T> clazz, byte[] bytes);

    <T> byte[] serialize(T object);
}
