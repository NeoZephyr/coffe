package net.tcp.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RpcResponseMessage extends Message {

    private Object value;

    private Exception exception;

    @Override
    public int getMsgType() {
        return RPC_RESPONSE_MESSAGE_TYPE;
    }
}
