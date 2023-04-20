package com.pain.apple.lab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_KEY = "_start_time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("request, {} {} {}", request.getRequestURL().toString(), request.getMethod(), request.getRemoteAddr());

        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_KEY, startTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long endTime = System.currentTimeMillis();
        long startTime = (long) request.getAttribute(START_TIME_KEY);
        log.info("request consume {} ms", (endTime - startTime));
    }
}