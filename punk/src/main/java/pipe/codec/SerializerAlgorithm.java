package pipe.codec;

public enum SerializerAlgorithm implements Serializer {
    JAVA {
        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            return null;
        }

        @Override
        public <T> byte[] serialize(T object) {
            return new byte[0];
        }
    }
    ;
}
