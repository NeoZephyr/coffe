package com.pain.green.aspectj;

import com.pain.green.annotation.Validate;
import com.pain.green.service.HelloService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AnnoAspect {

    @Pointcut(value = "execution(* com.pain.green.service.HelloServiceImpl.*(..))")
    public void helloServicePointcut() {}

    @Pointcut(value = "@annotation(com.pain.green.annotation.Validate)")
    public void validatePointcut() {}

    @Around(value = "helloServicePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("=== annotation around aspect before: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        System.out.println("=== annotation around aspect after: " + joinPoint.getSignature().getName());

        return result;
    }

    @AfterThrowing(value = "helloServicePointcut()", throwing = "ex")
    public void afterThrowing(Exception ex) {
        System.out.println("=== annotation afterThrowing aspect: " + ex.getMessage());
    }

    @AfterReturning(value = "helloServicePointcut()", returning = "how")
    public void afterReturning(String how) {
        System.out.println("=== annotation afterReturning aspect: " + how);
    }

    /**
     * 绑定代理对象
     * @param helloService
     */
    @Before("this(helloService)")
    public void bindProxy(HelloService helloService) {
        System.out.println("=== annotation before aspect: " + helloService);
    }

    @Before(value = "validatePointcut() && @annotation(validate) && args(word, name)")
    public void beforeValidate(Validate validate, String word, String name) {
        if (validate.value()) {
            System.out.printf("=== annotation before aspect: validate, word -> %s, name -> %s\n", word, name);
        } else {
            System.out.printf("=== annotation before aspect: no validate, word -> %s, name -> %s\n", word, name);
        }
    }
}