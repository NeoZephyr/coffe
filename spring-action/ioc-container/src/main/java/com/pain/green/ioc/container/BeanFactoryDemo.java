package com.pain.green.ioc.container;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

public class BeanFactoryDemo {
    public static void main(String[] args) {
        inspect();
    }

    @Configuration
    static class Config {

        @Bean
        public A a() {
            return new A();
        }

        @Bean
        public B b() {
            return new B();
        }

        @Bean
        public C1 c1() {
            return new C1();
        }

        @Bean
        public C2 c2() {
            return new C2();
        }
    }

    static class A {

        @Autowired
        public B b;

        // 优先级
        @Autowired
        @Resource(name = "c2")
        public Face c1;

        public A() {
            System.out.println("construct bean A");
        }
    }

    static class B {
        public B() {
            System.out.println("construct bean B");
        }
    }

    static interface Face {}

    static class C1 implements Face {}
    static class C2 implements Face {}

    /**
     * BeanFactory 不会主动做的工作
     * 1 不会主动调用 BeanFactory 后处理器
     * 2 不会主动添加 Bean 后处理器
     * 3 不会主动初始化单列
     * 4 不会解析 ${} #{} 占位符
     */
    private static void inspect() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope("singleton")
                .getBeanDefinition();

        // 添加 bean 定义：class, scope, 初始化
        beanFactory.registerBeanDefinition("config", beanDefinition);

        // 缺少解析 @Bean @Configuration 注解的能力
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        // 给 beanFactory 添加一些常用的后处理器
        // ConfigurationClassPostProcessor
        // AutowiredAnnotationBeanPostProcessor
        // CommonAnnotationBeanPostProcessor
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        // BeanFactoryPostProcessor 补充了一些 Bean 定义
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(bpp -> {
            bpp.postProcessBeanFactory(beanFactory);
        });

        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        // B b 没有自动注入
//        A a = beanFactory.getBean(A.class);
//        System.out.println(a.b);

        // Bean 后处理器，针对 Bean 生命周期的各个阶段提供扩展，如 @Autowired @Resource
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().stream()
                .sorted(beanFactory.getDependencyComparator()).forEach(bpp -> {
            // 查看 BeanPostProcessor 顺序
            System.out.println("BeanPostProcessor: " + bpp);
            beanFactory.addBeanPostProcessor(bpp);
        });

        // 提前准备好所有的单列
        beanFactory.preInstantiateSingletons();

        System.out.println("=== BeanFactory prepared");

        A a = beanFactory.getBean(A.class);
        System.out.println(a.b);
        System.out.println(a.c1);
    }

    private static void lookup() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        String location = "classpath:dependency-lookup-context.xml";
        int beanDefinitionCount = beanDefinitionReader.loadBeanDefinitions(location);

        System.out.println("beanDefinitionCount: " + beanDefinitionCount);

        lookupCollectionByType(beanFactory);
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> userMap = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("lookupCollectionByType: " + userMap);
        }
    }
}
