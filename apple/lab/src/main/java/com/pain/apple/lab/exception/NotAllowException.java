package com.pain.apple.lab.exception;

public class NotAllowException extends RuntimeException {
    public NotAllowException() {
        super();
    }

    public NotAllowException(String message) {
        super(message);
    }
}
