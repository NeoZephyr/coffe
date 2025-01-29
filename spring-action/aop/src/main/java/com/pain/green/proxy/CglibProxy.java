package com.pain.green.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy {

    static class Target {

        public void foo() {
            System.out.println(">>> 目标类 foo 方法调用");
        }
    }

    public static void main(String[] args) {
        Target target = new Target();

        // 代理 proxy 是 target 的子类，是父子关系
        // 如果 target 所属的是 final 类，或者方法是 final 的，那么就不能增强了
        Target proxy = (Target) Enhancer.create(Target.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println(">>> before...");
                Object result = method.invoke(target, objects); // 代理返回目标方法执行结果

                // methodProxy 可以避免反射调用方法
                // Object result = methodProxy.invoke(target, objects); // 传目标类参数
                // Object result = methodProxy.invokeSuper(o, objects); // 传代理参数
                System.out.println(">>> after...");
                return result;
            }
        });
        proxy.foo();
    }
}
