package com.pain.green.bean.scope;

import com.pain.green.ioc.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

public class ThreadLocalScopeDemo {

    @Bean
    @Scope(ThreadLocalScope.SCOPE_NAME)
    public User user() {
        User user = new User(System.nanoTime(), UUID.randomUUID().toString().substring(0, 5));
        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ThreadLocalScopeDemo.class);
        applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
            beanFactory.registerScope(ThreadLocalScope.SCOPE_NAME, new ThreadLocalScope());
        });
        applicationContext.refresh();

        lookup(applicationContext);
        lookupInThread(applicationContext);

        applicationContext.close();
    }

    private static void lookup(ApplicationContext applicationContext) {
        for (int i = 0; i < 3; ++i) {
            User user = applicationContext.getBean("user", User.class);
            System.out.printf("[Thread id: %d] bean: %s\n", Thread.currentThread().getId(), user);
        }
    }

    private static void lookupInThread(ApplicationContext applicationContext) {
        for (int i = 0; i < 3; ++i) {
            Thread thread = new Thread(() -> {
                User user = applicationContext.getBean("user", User.class);
                System.out.printf("[Thread id: %d] bean: %s\n", Thread.currentThread().getId(), user);
            });

            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
