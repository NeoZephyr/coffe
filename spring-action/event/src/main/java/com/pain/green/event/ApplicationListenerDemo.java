package com.pain.green.event;

import org.springframework.context.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAsync
public class ApplicationListenerDemo implements ApplicationEventPublisherAware {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ApplicationListenerDemo.class);
        applicationContext.register(MyApplicationListener.class);

        // 1. 基于 ApplicationListener 注册为 Bean
        // 2. API 方式注册
        applicationContext.addApplicationListener(event -> System.out.println("ApplicationListener: " + event.getClass().getSimpleName()));

        applicationContext.refresh();
        applicationContext.start();
        applicationContext.stop();
        applicationContext.close();
    }

    /**
     * aware 方式进行注入 ApplicationEventPublisher
     * 其实 ApplicationContext 本身也是 ApplicationEventPublisher
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new ApplicationEvent("hello event") {
        });

        // 会变成 PayloadApplicationEvent 事件
        // 简化 spring 事件发送
        applicationEventPublisher.publishEvent("hello event");

        applicationEventPublisher.publishEvent(new MyPayloadApplicationEvent(this, "hello event"));
    }

    static class MyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            System.out.println("MyApplicationListener: " + event.getClass().getSimpleName());
        }
    }

    // 应该谨慎使用 PayloadApplicationEvent，spring 本意是用作内部使用
    // 要这么定义才可以
    // static class MyPayloadApplicationEvent<String> extends PayloadApplicationEvent<String>
    static class MyPayloadApplicationEvent extends PayloadApplicationEvent<String> {

        public MyPayloadApplicationEvent(Object source, String payload) {
            super(source, payload);
        }
    }

    /**
     * 注解 @EventListener 处理：EventListenerMethodProcessor
     */
    @EventListener
    @Order(1)
    public void onApplicationEvent1(ContextRefreshedEvent event) {
        System.out.println("refresh1: " + event.getClass().getSimpleName());
    }

    @EventListener
    @Order(2)
    public void onApplicationEvent2(ContextRefreshedEvent event) {
        System.out.println("refresh2: " + event.getClass().getSimpleName());
    }

    /**
     * 接口的方式实现异步，需要在 SimpleApplicationEventMulticaster 关联异步线程池
     */
    @EventListener
    @Async
    public void onApplicationEvent3(ContextRefreshedEvent event) {
        System.out.println("thread: " + Thread.currentThread().getName() + ", refresh3: " + event.getClass().getSimpleName());
    }

    @EventListener
    public void onApplicationEvent(ContextStartedEvent event) {
        System.out.println("start: " + event.getClass().getSimpleName());
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("close: " + event.getClass().getSimpleName());
    }

    @Bean
    public Executor taskExecutor() {
        ExecutorService taskExecutor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("customize-spring-event-thread-pool"));
        return taskExecutor;
    }
}
