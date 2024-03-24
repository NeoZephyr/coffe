package com.pain.green.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
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

        applicationContext.addApplicationListener(event -> System.out.println("ApplicationListener: " + event.getClass().getSimpleName()));

        applicationContext.refresh();
        applicationContext.start();
        applicationContext.stop();
        applicationContext.close();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new ApplicationEvent("hello event") {
        });

        // PayloadApplicationEvent
        applicationEventPublisher.publishEvent("hello event");
    }

    static class MyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            System.out.println("MyApplicationListener: " + event.getClass().getSimpleName());
        }
    }

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
