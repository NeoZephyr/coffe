package com.pain.green.ioc.container;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationContextDemo {

    public static void main(String[] args) {
        // classPathXmlApplication();
        // fileSystemXmlApplication();

        // beanFactory();

        // annotationConfigApplicationContext();
        annotationConfigServletWebServerApplicationContext();
    }

    private static void classPathXmlApplication() {
        // 在 xml 加 <context:annotation-config /> 配置，会加入一些后处理器
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("dependency-lookup-context.xml");

        for (String name : ctx.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        System.out.println(ctx.getBean("user"));
    }

    private static void fileSystemXmlApplication() {
        FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext("ioc-container/src/main/resources/dependency-lookup-context.xml");

        for (String name : ctx.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        System.out.println(ctx.getBean("user"));
    }

    private static void beanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        System.out.println("before load...");

        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // reader.loadBeanDefinitions(new ClassPathResource("dependency-lookup-context.xml"));
        reader.loadBeanDefinitions(new FileSystemResource("ioc-container/src/main/resources/dependency-lookup-context.xml"));

        System.out.println("after load...");

        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }

    private static void annotationConfigApplicationContext() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);

        for (String name : ctx.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }

    private static void annotationConfigServletWebServerApplicationContext() {
        AnnotationConfigServletWebServerApplicationContext ctx = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
    }

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

    @Configuration
    static class WebConfig {

        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        // http://localhost:8080/hello
        @Bean("/hello")
        public Controller controller() {
            return (httpServletRequest, httpServletResponse) -> {
                httpServletResponse.getWriter().print("hello");
                return null;
            };
        }
    }
}
