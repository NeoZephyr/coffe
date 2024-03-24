package com.pain.green.aop.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AroundAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("=== around before: " + methodInvocation.getMethod().getName());
        Object result = methodInvocation.proceed();
        System.out.println("=== around after: " + methodInvocation.getMethod().getName());
        return result;
    }
}
