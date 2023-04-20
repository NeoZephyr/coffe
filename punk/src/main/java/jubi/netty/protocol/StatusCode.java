package jubi.netty.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum StatusCode {
    SUCCESS(0),
    INTERNAL_ERROR(1),
    TIMEOUT(2),
    ACCESS_DENIED(3),
    INVALID_REQUEST(4),
    UNKNOWN(-1);

    static final Map<Integer, StatusCode> VALUE_MAP =
            Arrays.stream(StatusCode.values()).collect(Collectors.toMap(StatusCode::code, s -> s));

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static StatusCode fromCode(Integer code) {
        StatusCode statusCode = VALUE_MAP.get(code);
        return statusCode == null ? UNKNOWN : statusCode;
    }
}
