package jubi.netty.protocol;

import java.io.*;
import java.nio.ByteBuffer;

public class JavaSerializer<T> implements MessageSerializer<T> {

    @Override
    public ByteBuffer serialize(T message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        oos.close();
        return ByteBuffer.wrap(baos.toByteArray());
    }

    @Override
    public T deserialize(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (T) ois.readObject();
    }

    static class User implements Serializable {
        int id;
        String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        User user = new User(1, "jack");
        JavaSerializer<User> serializer = new JavaSerializer<>();
        ByteBuffer buffer = serializer.serialize(user);
        User user1 = serializer.deserialize(buffer);
        System.out.println(user1);
    }
}
