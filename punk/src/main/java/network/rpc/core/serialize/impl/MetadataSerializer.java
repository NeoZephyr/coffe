package network.rpc.core.serialize.impl;

import network.rpc.core.nameservice.Metadata;
import network.rpc.core.serialize.Serializer;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetadataSerializer implements Serializer<Metadata> {

    @Override
    public int size(Metadata entry) {
        return Short.BYTES + entry.entrySet().stream().mapToInt(this::entrySize).sum();
    }

    @Override
    public void serialize(Metadata entry, byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        buffer.putShort(toShortSafely(entry.size()));

        entry.forEach((k, v) -> {
            byte[] bs = k.getBytes(StandardCharsets.UTF_8);
            buffer.putShort(toShortSafely(bs.length));
            buffer.put(bs);

            buffer.putShort(toShortSafely(v.size()));

            for (URI uri : v) {
                bs = uri.toASCIIString().getBytes(StandardCharsets.UTF_8);
                buffer.putShort(toShortSafely(bs.length));
                buffer.put(bs);
            }
        });
    }

    @Override
    public Metadata parse(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        Metadata metadata = new Metadata();
        int size = buffer.getShort();

        for (int i = 0; i < size; i++) {
            int keyLen = buffer.getShort();
            byte[] bs = new byte[keyLen];
            buffer.get(bs);
            String key = new String(bs, StandardCharsets.UTF_8);

            int valueSize = buffer.getShort();
            List<URI> uriList = new ArrayList<>(valueSize);

            for (int j = 0; j < valueSize; j++) {
                int uriLen = buffer.getShort();
                bs = new byte[uriLen];
                buffer.get(bs);
                URI uri = URI.create(new String(bs, StandardCharsets.UTF_8));
                uriList.add(uri);
            }

            metadata.put(key, uriList);
        }

        return metadata;
    }

    @Override
    public byte type() {
        return Types.TYPE_METADATA;
    }

    @Override
    public Class<Metadata> getEntryClass() {
        return Metadata.class;
    }

    private int entrySize(Map.Entry<String, List<URI>> e) {
        return Short.BYTES +
                e.getKey().getBytes().length +
                Short.BYTES +
                e.getValue().stream()
                        .mapToInt(uri ->
                                Short.BYTES + uri.toASCIIString().getBytes(StandardCharsets.UTF_8).length
                        ).sum();
    }

    private short toShortSafely(int v) {
        assert v < Short.MAX_VALUE;
        return (short) v;
    }
}