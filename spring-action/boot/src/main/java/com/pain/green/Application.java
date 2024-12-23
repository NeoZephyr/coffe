package com.pain.green;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Clock;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // BeanFactory 是 ApplicationContext 的父接口
        // ApplicationContext 一部分功能组合了 BeanFactory 的功能
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        // 管理单列对象
        Field field = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        field.setAccessible(true);
        Map<String, Object> map = (Map<String, Object>) field.get(beanFactory);
        map.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });

        // ApplicationContext 继承接口
        // MessageSource, ResourcePatternResolver, ApplicationEventPublisher, EnvironmentCapable

        // 国际化
        System.out.println(context.getMessage("hello", null, Locale.CHINA));

        // 根据通配符获取资源（类路径、磁盘路径）
        Resource[] resources = context.getResources("classpath*:META-INF/spring.factories");

        for (Resource resource : resources) {
            System.out.println(resource);
        }

        // 环境变量
        System.out.println(context.getEnvironment().getProperty("JAVA_HOME"));;
        System.out.println(context.getEnvironment().getProperty("server.port"));

        // 发布事件，进行解藕
        context.publishEvent(new FooEvent("application"));
    }

    @Component
    static class Bean {

        // @EventListener 注解方法接受事件
        @EventListener
        public void receive(FooEvent event) {
            System.out.println(event);
        }
    }

    static class FooEvent extends ApplicationEvent {
        public FooEvent(Object source) {
            super(source);
        }

        public FooEvent(Object source, Clock clock) {
            super(source, clock);
        }
    }
}
