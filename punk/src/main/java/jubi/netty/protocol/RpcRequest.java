package jubi.netty.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.ByteBuffer;

@Data
@EqualsAndHashCode(callSuper=false)
public class RpcRequest extends AbstractMessage implements RequestMessage {

    private long requestId;
    private ByteBuffer body;

    public RpcRequest(long requestId, ByteBuffer body) {
        this.requestId = requestId;
        this.body = body;
    }

    @Override
    public Type type() {
        return Type.RPC_REQUEST;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(requestId);
        buf.writeBytes(body);
    }

    public static RpcRequest decode(ByteBuf buf) {
        long requestId = buf.readLong();
        return new RpcRequest(requestId, buf.retain().nioBuffer());
    }
}
