package com.pain.green.aspectj;

import com.pain.green.service.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AspectjDemo {
    public static void main(String[] args) {
        // xmlAspect();
        annoAspect();
    }

    private static void xmlAspect() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/aspectj.xml");
        HelloService helloService = (HelloService) context.getBean("helloService");
        helloService.hello();
        helloService.morning();
    }

    private static void annoAspect() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AspectjConfig.class);
        HelloService helloService = context.getBean(HelloService.class);
        // helloService.hello();
        // helloService.morning();
        // helloService.how();
        helloService.hello("吃饭了没有", "bloom");
    }
}
