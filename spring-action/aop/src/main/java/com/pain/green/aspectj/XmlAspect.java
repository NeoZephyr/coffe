package com.pain.green.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;

public class XmlAspect {

    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("=== xml around aspect before: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        System.out.println("=== xml around aspect after: " + joinPoint.getSignature().getName());

        return result;
    }
}
