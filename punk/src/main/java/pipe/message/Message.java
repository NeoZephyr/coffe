package pipe.message;

import lombok.Data;

@Data
public abstract class Message {
    public int seqId;
    public int msgType;

    public static final int RPC_REQUEST_MESSAGE_TYPE = 1;
    public static final int RPC_RESPONSE_MESSAGE_TYPE = 2;

    public abstract int getMsgType();

    public static Class<? extends Message> getMsgClass(int msgType) {
        if (msgType == RPC_REQUEST_MESSAGE_TYPE) {
            return RpcRequestMessage.class;
        }

        throw new IllegalArgumentException("msgType " + msgType + " is invalid");
    }
}