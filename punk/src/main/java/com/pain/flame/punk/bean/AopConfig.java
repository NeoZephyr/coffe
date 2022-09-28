package com.pain.flame.punk.bean;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopConfig {

    @Before("execution(* com.pain.flame.punk.service.UserService.pay())")
    public void validate(JoinPoint joinPoint) throws InterruptedException {
        System.out.println("=== validate");
        Thread.sleep(1000);
    }

    @Around("execution(* com.pain.flame.punk.service.UserService.pay())")
    public void performance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        joinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.printf("=== performance, cost %s(ms)\n", (end - start));
    }
}
