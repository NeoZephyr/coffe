package jubi.netty.protocol;

import io.netty.buffer.ByteBuf;

public abstract class Message implements Encodable {

    public abstract Type type();

    public enum Type implements Encodable {
        UNKNOWN_TYPE(-1),
        RPC_RESPONSE(0)
        ;

        private final byte id;

        Type(int id) {
            assert id < 128 : "Cannot have more than 128 message types";
            this.id = (byte) id;
        }

        public byte id() {
            return id;
        }

        @Override
        public int encodedLength() {
            return 1;
        }

        @Override
        public void encode(ByteBuf buf) {
            buf.writeByte(id);
        }

        public static Type decode(ByteBuf buf) {
            byte id = buf.readByte();
            switch (id) {
                case 0:
                    return RPC_RESPONSE;
                case -1:
                    throw new IllegalArgumentException("User type message cannot be decoded.");
                default:
                    throw new IllegalArgumentException("Unknown message type: " + id);
            }
        }
    }

    public static Message decode(Type type, ByteBuf buf) {
        switch (type) {
            case RPC_RESPONSE:
                return RpcResponse.decode(buf);
            default:
                throw new IllegalArgumentException("Unexpected message type: " + type);
        }
    }
}