package com.pain.green.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * 层次性 spring 事件传播
 */
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

        // childContext 刷新，会有两个上下文进行事件传播，一个是自己，一个是 parent
        childContext.refresh();

        // 注意 parent 与 child 的调用顺序不一样，结果也不一样
        parentContext.close();
        childContext.close();
    }

    static class MyListener implements ApplicationListener<ApplicationContextEvent> {

        /**
         * 确保事件一次处理
         *
         * 也可以注入 ApplicationContext，判断事件的上下文对象是否是当前的上下文对象。从而进行过滤
         * https://github.com/alibaba/spring-context-support/blob/master/src/main/java/com/alibaba/spring/context/OnceApplicationContextEventListener.java
         */
        static Set<ApplicationContextEvent> applicationContextEvents = new HashSet<>();

        @Override
        public void onApplicationEvent(ApplicationContextEvent event) {
            if (applicationContextEvents.add(event)) {
                System.out.printf("context id: %s, event: %s\n", event.getApplicationContext().getId(), event.getClass().getSimpleName());
            }
        }
    }
}
