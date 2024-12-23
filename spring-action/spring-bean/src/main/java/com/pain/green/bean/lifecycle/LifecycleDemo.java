package com.pain.green.bean.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class LifecycleDemo {

    public static void main(String[] args) {
        // AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(new DefaultListableBeanFactory());
        ctx.register(Config.class);
        ctx.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        ctx.refresh();

        ctx.close();
    }

    @Configuration
    static class Config {
        @Bean
        MyBean myBean() {
            return new MyBean();
        }

        @Bean
        MyBeanPostProcessor myBeanPostProcessor() {
            return new MyBeanPostProcessor();
        }
    }

    static class MyBean {
        public MyBean() {
            System.out.println("construct");
        }

        @Autowired
        public void autowire(@Value("${SPARK_HOME}") String home) {
            // 这里没有能读取到环境变量
            // 可能时候 shell 的原因
            System.out.println("autowired env home: " + home);
        }

        @PostConstruct
        public void init() {
            System.out.println("post construct");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("pre destroy");
        }
    }

    static class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, DestructionAwareBeanPostProcessor {

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 销毁之前执行，比如 @PreDestroy");
            }
        }

        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 实例化之前执行，这里返回的对象会替换原本的 bean");
            }

            return null;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 实例化之后执行，如果返回 false 会替换掉原本的 bean");
            }

            return true;
        }

        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 依赖注入阶段执行，比如 @Autowired、@Value、@Resource");
            }

            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 初始化之前执行，这里返回的对象会替换掉原本的 bean，如 @PostConstruct、@ConfigurationProperties");
            }

            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 初始化之后执行，这里返回的对象会替换掉原本的 bean，如代理增强");
            }

            return bean;
        }
    }
}
