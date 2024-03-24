package com.pain.green.ioc.container;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

// @Configuration
public class AnnotationApplicationContextDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationApplicationContextDemo.class);
        applicationContext.refresh();
        lookupCollectionByType(applicationContext);

        applicationContext.close();
    }

    @Bean
    public User user() {
        User user = new User();
        user.setId(2L);
        user.setName("高欢");

        return user;
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> userMap = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("lookupCollectionByType: " + userMap);
        }
    }
}
