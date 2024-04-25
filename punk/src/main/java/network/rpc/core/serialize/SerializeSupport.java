package network.rpc.core.serialize;

import lombok.extern.slf4j.Slf4j;
import network.rpc.api.ServiceSupport;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SerializeSupport {

    // 序列化对象类型 -> 序列化实现
    private static Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    // 序列化实现类型 -> 序列化对象类型
    private static Map<Byte, Class<?>> types = new HashMap<>();

    static {
        for (Serializer serializer : ServiceSupport.loadAll(Serializer.class)) {
            registerType(serializer.type(), serializer.getEntryClass(), serializer);

            log.info("Found serializer, class: {}, type: {}",
                    serializer.getEntryClass().getCanonicalName(), serializer.type());
        }
    }

    public static <T> T parse(byte[] buf) {
        return parse(buf, 0, buf.length);
    }

    public static <T> byte[] serialize(T entry) {
        Serializer<T> serializer = (Serializer<T>) serializers.get(entry.getClass());

        if (serializer == null) {
            throw new SerializeException(String.format("Unknown entry class type: %s", entry.getClass().toString()));
        }

        byte[] bytes = new byte[serializer.size(entry) + 1];
        bytes[0] = serializer.type();
        serializer.serialize(entry, bytes, 1, bytes.length - 1);
        return bytes;
    }

    private static byte parseType(byte[] buf) {
        return buf[0];
    }

    private static <T> void registerType(byte type, Class<T> clazz, Serializer<T> serializer) {
        serializers.put(clazz, serializer);
        types.put(type, clazz);
    }

    private static <T> T parse(byte[] buf, int offset, int length) {
        byte type = parseType(buf);
        Class<T> clazz = (Class<T>) types.get(type);

        if (null == clazz) {
            throw new SerializeException(String.format("Unknown entry type: %d!", type));
        } else {
            return parse(buf, offset + 1, length - 1, clazz);
        }
    }

    private static <T> T parse(byte[] buf, int offset, int length, Class<T> clazz) {
        Object entry = serializers.get(clazz).parse(buf, offset, length);

        if (clazz.isAssignableFrom(entry.getClass())) {
            return (T) entry;
        } else {
            throw new SerializeException("Type mismatch!");
        }
    }
}