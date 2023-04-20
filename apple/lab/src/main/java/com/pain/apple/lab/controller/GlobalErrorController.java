package com.pain.apple.lab.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes attributes;

    @RequestMapping(value = "/error", produces = "text/html")
    public String errorHtml(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        log.error("error: {}", attributes);
        log.error("status: {}, exception: {}", status, exception);
        return "error";
    }

    @RequestMapping(value = "/error")
    @ResponseBody
    public Map<String, Object> error(HttpServletRequest request) {
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        log.error("error: {}, exception: {}", attributes, exception);

        Map<String, Object> result = new HashMap<>();
        result.put("body", "hello");
        return result;
    }
}
