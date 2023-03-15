package net.tcp.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 一定要可以序列化，不然啥错误都没有
 */
@Data
public abstract class Message implements Serializable {
    public int seqId;
    public int msgType;

    // 时间戳
    // private int timeStamp;

    public static final int RPC_REQUEST_MESSAGE_TYPE = 1;
    public static final int RPC_RESPONSE_MESSAGE_TYPE = 2;

    public abstract int getMsgType();

    public static Class<? extends Message> getMsgClass(int msgType) {
        if (msgType == RPC_REQUEST_MESSAGE_TYPE) {
            return RpcRequestMessage.class;
        }

        if (msgType == RPC_RESPONSE_MESSAGE_TYPE) {
            return RpcResponseMessage.class;
        }

        throw new IllegalArgumentException("msgType " + msgType + " is invalid");
    }
}