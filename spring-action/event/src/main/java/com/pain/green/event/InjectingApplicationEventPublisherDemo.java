package com.pain.green.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.PostConstruct;

public class InjectingApplicationEventPublisherDemo implements ApplicationEventPublisherAware, ApplicationContextAware {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        // 关注事件发布顺序
        applicationEventPublisher.publishEvent(new MySpringEvent("Event from @Autowired ApplicationEventPublisher"));

        // ApplicationContext 实现了 ApplicationEventPublisher 接口
        // ApplicationEventPublisher 与 ApplicationEventMulticaster 之间没有层级关系，属于弱联系
        applicationContext.publishEvent(new MySpringEvent("Event from @Autowired ApplicationContext"));
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(InjectingApplicationEventPublisherDemo.class);
        context.addApplicationListener(new MySpringEventListener());
        context.refresh();
        context.close();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // 如果这个 bean 是 BeanPostProcessor 或者 BeanFactoryPostProcessor（实现这个接口）初始化比较早
        // 然后事件可能就是早期事件 要存入 earlyApplicationEvents 一切 ready 之后进行回放
        applicationEventPublisher.publishEvent(new MySpringEvent("Event from ApplicationEventPublisherAware"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.publishEvent(new MySpringEvent("Event from ApplicationContextAware"));
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

    static class MySpringEventListener implements ApplicationListener<MySpringEvent> {
        @Override
        public void onApplicationEvent(MySpringEvent event) {
            System.out.printf("listener, thread: %s, event: %s\n", Thread.currentThread().getName(), event);
        }
    }
}
