package com.pain.green.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

import java.util.HashSet;
import java.util.Set;

public class HierarchicalSpringEventPropagateDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext();
        parentContext.setId("parent-context");
        parentContext.register(MyListener.class);

        AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setId("child-context");
        childContext.register(MyListener.class);

        childContext.setParent(parentContext);

        parentContext.refresh();
        childContext.refresh();

        parentContext.close();
        childContext.close();
    }

    static class MyListener implements ApplicationListener<ApplicationContextEvent> {

        static Set<ApplicationContextEvent> applicationContextEvents = new HashSet<>();

        @Override
        public void onApplicationEvent(ApplicationContextEvent event) {
            if (applicationContextEvents.add(event)) {
                System.out.printf("context id: %s, event: %s\n", event.getApplicationContext().getId(), event.getClass().getSimpleName());
            }
        }
    }
}
