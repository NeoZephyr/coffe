package net.tcp.codec;

import java.io.*;

public enum SerializerAlgorithm implements Serializer {
    JAVA {
        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            try {
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (T) is.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return null;
            }
        }

        @Override
        public <T> byte[] serialize(T object) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(baos);
                os.writeObject(object);
                return baos.toByteArray();
            } catch (IOException e) {
                return null;
            }
        }
    }
}