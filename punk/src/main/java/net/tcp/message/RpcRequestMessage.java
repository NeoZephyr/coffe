package net.tcp.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RpcRequestMessage extends Message {

    @Override
    public int getMsgType() {
        return RPC_REQUEST_MESSAGE_TYPE;
    }

    private String face;

    private String method;

    private Class<?> returnType;

    private Class<?>[] paramTypes;

    private Object[] paramValues;

    public RpcRequestMessage(int seqId, String face, String method, Class<?> returnType, Class<?>[] paramTypes, Object[] paramValues) {
        this.seqId = seqId;
        this.face = face;
        this.method = method;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.paramValues = paramValues;
    }
}
