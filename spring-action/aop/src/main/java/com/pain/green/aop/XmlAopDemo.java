package com.pain.green.aop;

import com.pain.green.service.HelloService;
import com.pain.green.service.HelloServiceImpl;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class XmlAopDemo {

    public static void main(String[] args) {
        // advice();
        // nameAdvice();
        // defaultAdvisor();
        // customAdvisor();

        // jdkProxy();
        cglibProxy();
    }

    private static void advice() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/aop-advice.xml");
        HelloService helloService = (HelloService) context.getBean("helloServiceProxy");
        helloService.hello();
        helloService.morning();
    }

    private static void nameAdvice() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/aop-name-advice.xml");
        HelloService helloService = (HelloService) context.getBean("helloService");
        helloService.hello();
        helloService.morning();
    }

    private static void defaultAdvisor() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/aop-default-advisor.xml");
        HelloService helloService = (HelloService) context.getBean("helloService");
        helloService.hello();
        helloService.morning();
    }

    private static void regexAdvisor() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/aop-regex-advisor.xml");
        HelloService helloService = (HelloService) context.getBean("helloServiceProxy");
        helloService.hello();
        helloService.morning();
    }

    private static void customAdvisor() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/aop-custom-advisor.xml");
        HelloService helloService = (HelloService) context.getBean("helloServiceProxy");
        helloService.hello();
        helloService.morning();
    }

    private static void jdkProxy() {
        HelloService helloService = new HelloServiceImpl();
        HelloService proxyHelloService = (HelloService) Proxy.newProxyInstance(
                helloService.getClass().getClassLoader(),
                helloService.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    System.out.println("=== begin");
                    Object result = method.invoke(helloService, args);
                    System.out.println("=== end");
                    return result;
                });
        proxyHelloService.hello();
        proxyHelloService.morning();
    }

    private static void cglibProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloServiceImpl.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("=== begin");
                Object result = methodProxy.invokeSuper(o, objects);
                System.out.println("=== end");
                return result;
            }
        });
        HelloService helloService = (HelloService) enhancer.create();
        helloService.hello();
        helloService.morning();
    }
}
