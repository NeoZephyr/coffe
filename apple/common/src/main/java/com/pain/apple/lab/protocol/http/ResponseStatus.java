package com.pain.apple.lab.protocol.http;

public class ResponseStatus {
    private final int code;
    private final String msg;

    public static ResponseStatus SUCCESS = new ResponseStatus(0, "success");

    public ResponseStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
