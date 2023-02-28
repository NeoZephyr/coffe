package com.pain.apple.lab.config;

import com.pain.apple.lab.exception.NotAllowException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class NotAllowExceptionHandler {

    @ExceptionHandler(NotAllowException.class)
    @ResponseBody
    public String handle() {
        System.out.println("403");
        return "{\"resultCode\": 403}";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public String handle404() {
        return "{\"resultCode\": 404}";
    }
}
