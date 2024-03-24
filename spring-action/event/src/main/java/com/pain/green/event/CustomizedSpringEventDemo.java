package com.pain.green.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.GenericApplicationContext;

public class CustomizedSpringEventDemo {
    public static void main(String[] args) {
        GenericApplicationContext applicationContext = new GenericApplicationContext();

        applicationContext.addApplicationListener(new MySpringEventListener1());
        applicationContext.addApplicationListener(new MySpringEventListener2());

        applicationContext.refresh();

        applicationContext.publishEvent(new MySpringEvent1("hello my spring event1"));
        applicationContext.publishEvent(new MySpringEvent2("hello my spring event2"));
    }

    static class MySpringEventListener1 implements ApplicationListener<MySpringEvent1> {
        @Override
        public void onApplicationEvent(MySpringEvent1 event) {
            System.out.printf("listener1, thread: %s, event: %s\n", Thread.currentThread().getName(), event);
        }
    }

    static class MySpringEventListener2 implements ApplicationListener<ApplicationEvent> {

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            System.out.printf("listener2, thread: %s, event: %s\n", Thread.currentThread().getName(), event);
        }
    }

    static class MySpringEvent1 extends ApplicationEvent {

        public MySpringEvent1(Object source) {
            super(source);
        }

        @Override
        public String getSource() {
            return (String) super.getSource();
        }
    }

    static class MySpringEvent2 extends ApplicationEvent {

        public MySpringEvent2(Object source) {
            super(source);
        }

        @Override
        public String getSource() {
            return (String) super.getSource();
        }
    }
}
