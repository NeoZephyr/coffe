package com.pain.green.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class ApplicationEventMulticasterDemo {
    public static void main(String[] args) {
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.addApplicationListener(new MySpringEventListener());
        applicationContext.refresh();

        ApplicationEventMulticaster multicaster = applicationContext.getBean(
                AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
                ApplicationEventMulticaster.class);

        if (multicaster instanceof SimpleApplicationEventMulticaster) {
            SimpleApplicationEventMulticaster simpleMulticaster = (SimpleApplicationEventMulticaster) multicaster;
            ExecutorService taskExecutor = newSingleThreadExecutor(new CustomizableThreadFactory("customize-spring-event-thread-pool"));

            // 同步 -> 异步
            simpleMulticaster.setTaskExecutor(taskExecutor);

            multicaster.addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> {
                System.out.printf("context closed event listener, thread: %s, event: %s\n", Thread.currentThread().getName(), event);
                if (!taskExecutor.isShutdown()) {
                    taskExecutor.shutdown();
                }
            });

            simpleMulticaster.setErrorHandler(e -> {
                System.err.println("spring event error：" + e.getMessage());
            });
        }

        applicationContext.publishEvent(new MySpringEvent("hello SimpleApplicationEventMulticaster"));
        applicationContext.close();
    }

    static class MySpringEventListener implements ApplicationListener<MySpringEvent> {

        @Override
        public void onApplicationEvent(MySpringEvent event) {
            System.out.printf("my spring event listener, thread: %s, event: %s\n", Thread.currentThread().getName(), event);
            throw new RuntimeException("抛出异常");
        }
    }

    static class MySpringEvent extends ApplicationEvent {

        public MySpringEvent(Object source) {
            super(source);
        }

        @Override
        public String getSource() {
            return (String) super.getSource();
        }
    }
}
