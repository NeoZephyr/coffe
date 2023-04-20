package com.pain.apple.lab.controller;

import com.pain.apple.lab.protocol.http.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handle(Exception ex){
        log.error("system error", ex);

        Map<String, Object> info = new HashMap<>();
        info.put("message", ex.getMessage());
        info.put("time", new Date().getTime());
        return info;
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public BaseResponse validExceptionHandler(BindException e) {
        String defaultMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();

        log.warn("参数校验失败：{}", defaultMessage);

        return BaseResponse.error(400, defaultMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<String> handleError(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
        String message = String.format("%s:%s", path, violation.getMessage());
        return BaseResponse.error();
    }
}
