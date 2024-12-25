package com.pain.green.ioc.container;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

public class BeanFactoryPostProcessorDemo {

    public static void main(String[] args) {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean("config", Config.class);
        ctx.registerBean(ConfigurationClassPostProcessor.class); // @Configuration @ComponentScan @Bean @Import @ImportSource

        // 常用的 BeanFactoryPostProcessor 还有 MyBatis 的 MapperScannerConfigurer

        ctx.refresh();

        for (String name : ctx.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        ctx.close();
    }

    @ComponentScan("com.pain.green.ioc.domain")
    @Configuration
    static class Config {

        @Bean
        public A a(B b) {
            A a = new A();
            a.b = b;
            return a;
        }

        @Bean
        public B b() {
            return new B();
        }
    }

    static class A {

        public B b;

        public A() {
            System.out.println("construct bean A");
        }
    }

    static class B {
        public B() {
            System.out.println("construct bean B");
        }
    }
}
