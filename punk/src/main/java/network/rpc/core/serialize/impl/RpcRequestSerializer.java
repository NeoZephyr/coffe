package network.rpc.core.serialize.impl;

import network.rpc.core.client.stubs.RpcRequest;
import network.rpc.core.serialize.Serializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RpcRequestSerializer implements Serializer<RpcRequest> {
    @Override
    public int size(RpcRequest entry) {
        return Integer.BYTES + entry.getServiceName().getBytes(StandardCharsets.UTF_8).length +
                Integer.BYTES + entry.getMethodName().getBytes(StandardCharsets.UTF_8).length +
                Integer.BYTES + entry.getArgs().length;
    }

    @Override
    public void serialize(RpcRequest entry, byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        byte[] bs = entry.getServiceName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(bs.length);
        buffer.put(bs);

        bs = entry.getMethodName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(bs.length);
        buffer.put(bs);

        bs = entry.getArgs();
        buffer.putInt(bs.length);
        buffer.put(bs);
    }

    @Override
    public RpcRequest parse(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        int len = buffer.getInt();
        byte[] bs = new byte[len];
        buffer.get(bs);
        String serviceName = new String(bs, StandardCharsets.UTF_8);

        len = buffer.getInt();
        bs = new byte[len];
        buffer.get(bs);
        String methodName = new String(bs, StandardCharsets.UTF_8);

        len = buffer.getInt();
        bs = new byte[len];
        buffer.get(bs);
        byte[] args = bs;

        return new RpcRequest(serviceName, methodName, args);
    }

    @Override
    public byte type() {
        return Types.TYPE_RPC_REQUEST;
    }

    @Override
    public Class<RpcRequest> getEntryClass() {
        return RpcRequest.class;
    }
}