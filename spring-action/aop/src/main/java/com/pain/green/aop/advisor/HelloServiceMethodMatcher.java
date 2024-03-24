package com.pain.green.aop.advisor;

import org.springframework.aop.MethodMatcher;

import java.lang.reflect.Method;

public class HelloServiceMethodMatcher implements MethodMatcher {
    @Override
    public boolean matches(Method method, Class<?> aClass) {
        String methodName = method.getName();

        if (methodName.contains("morning")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> aClass, Object... objects) {
        return false;
    }
}
