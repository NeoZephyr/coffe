package jubi.netty.protocol;

import io.netty.buffer.ByteBuf;

public class RpcFailure extends AbstractMessage implements ResponseMessage {

    public final long requestId;
    public final String errorMsg;

    public RpcFailure(long requestId, String errorMsg) {
        this.requestId = requestId;
        this.errorMsg = errorMsg;
    }

    @Override
    public Message.Type type() { return Type.RPC_FAILURE; }

    public long getRequestId() {
        return requestId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public void encode(ByteBuf buf) {

    }
}
