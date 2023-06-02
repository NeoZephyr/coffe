package jubi.netty.protocol;

import io.netty.buffer.ByteBuf;
import jubi.netty.util.ByteBufUtils;

import java.util.Objects;

public class RpcResponse extends AbstractMessage implements ResponseMessage {

    private long requestId;
    private StatusCode statusCode;
    private String message;

    public RpcResponse(long requestId, StatusCode statusCode) {
        this(requestId, statusCode, null);
    }

    public RpcResponse(long requestId, StatusCode statusCode, String message) {
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.message = message;
    }

    public static RpcResponse decode(ByteBuf buf) {
        long requestId = buf.readLong();
        StatusCode statusCode = StatusCode.fromCode(buf.readInt());
        String message = ByteBufUtils.readLengthAndString(buf);
        return new RpcResponse(requestId, statusCode, message);
    }

    @Override
    public Type type() {
        return Type.RPC_RESPONSE;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId=" + requestId +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcResponse that = (RpcResponse) o;
        return requestId == that.requestId &&
                statusCode == that.statusCode &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, statusCode, message);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public long getRequestId() {
        return requestId;
    }

    @Override
    public void encode(ByteBuf buf) {

    }
}