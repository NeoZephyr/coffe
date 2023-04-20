package com.pain.apple.lab.protocol.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseResponse<T> {
    private boolean success;
    private int code;
    private String msg;
    private T data;

    public static <T> BaseResponse<T> success() {
        return ResponseBuilder.<T>newBuilder().success(true).build();
    }

    public static <T> BaseResponse<T> success(T data) {
        return ResponseBuilder.<T>newBuilder()
                .success(true)
                .code(ResponseStatus.SUCCESS.getCode())
                .msg(ResponseStatus.SUCCESS.getMsg())
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> success(int code, String msg) {
        return ResponseBuilder.<T>newBuilder()
                .success(true)
                .code(code)
                .msg(msg).build();
    }

    public static <T> BaseResponse<T> success(int code, String msg, T data) {
        return ResponseBuilder.<T>newBuilder()
                .success(true)
                .code(code)
                .msg(msg)
                .data(data).build();
    }

    public static <T> BaseResponse<T> error() {
        return ResponseBuilder.<T>newBuilder().success(false).build();
    }

    public static <T> BaseResponse<T> error(int code, String msg) {
        return ResponseBuilder.<T>newBuilder()
                .success(false)
                .code(code)
                .msg(msg).build();
    }

    public static <T> BaseResponse<T> error(int code, String msg, T data) {
        return ResponseBuilder.<T>newBuilder()
                .success(false)
                .code(code)
                .msg(msg)
                .data(data).build();
    }

    public static <T> BaseResponse<T> error(ResponseStatus status) {
        return ResponseBuilder.<T>newBuilder()
                .success(false)
                .code(status.getCode())
                .msg(status.getMsg()).build();
    }

    static class ResponseBuilder<U> {

        private int code;
        private String msg;
        private boolean success;
        private U data;

        static <U> ResponseBuilder<U> newBuilder() {
            return new ResponseBuilder<U>();
        }

        ResponseBuilder<U> code(int code) {
            this.code = code;
            return this;
        }

        ResponseBuilder<U> msg(String msg) {
            this.msg = msg;
            return this;
        }

        ResponseBuilder<U> success(boolean success) {
            this.success = success;
            return this;
        }

        ResponseBuilder<U> data(U data) {
            this.data = data;
            return this;
        }

        BaseResponse<U> build() {
            return new BaseResponse<>(success, code, msg, data);
        }
    }
}