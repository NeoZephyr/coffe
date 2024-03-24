package com.pain.green.ioc.dependency.source;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.PostConstruct;

public class ResolvableDependencySourceDemo {

    @Autowired
    private User user;

    @PostConstruct
    public void init() {
        System.out.println("inject user: " + user);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ResolvableDependencySourceDemo.class);
        applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
            beanFactory.registerResolvableDependency(User.class, new User(10L, "朱一旦"));
        });
        applicationContext.refresh();

        applicationContext.close();
    }
}
