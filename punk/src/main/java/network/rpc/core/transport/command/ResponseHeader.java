package network.rpc.core.transport.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.charset.StandardCharsets;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResponseHeader extends Header {
    private int code;
    private String error;

    public ResponseHeader(int type, int version, int requestId,  Throwable throwable) {
        this(type, version, requestId, Code.UNKNOWN_ERROR.getCode(), throwable.getMessage());
    }

    public ResponseHeader(int type, int version, int requestId) {
        this(type, version, requestId, Code.SUCCESS.getCode(), null);
    }

    public ResponseHeader( int type, int version, int requestId , int code, String error) {
        super(type, version, requestId);
        this.code = code;
        this.error = error;
    }

    @Override
    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES +
                Integer.BYTES +
                (error == null ? 0 : error.getBytes(StandardCharsets.UTF_8).length);
    }
}