package com.pain.green.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class FooAspect {

    @Before("execution(* com.pain.green.service.FooService.foo())")
    public void before() {
        System.out.println(">>> 切面 before 调用");
    }
}
