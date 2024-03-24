package com.pain.green.ioc.dependency.lookup;

import com.pain.green.ioc.annotation.Super;
import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class DependencyLookupDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:dependency-lookup-context.xml");

        lookupById(beanFactory);
        lookupByType(beanFactory);
        lookupInLazy(beanFactory);
        lookupByProvider(beanFactory);
        lookupByProviderIfAvailable(beanFactory);
        lookupByProviderStream(beanFactory);
        lookupCollectionByType(beanFactory);
        lookupByAnnotation(beanFactory);
    }

    private static void lookupById(BeanFactory beanFactory) {
        User user = (User) beanFactory.getBean("user");
        System.out.println("lookupById: " + user);
    }

    private static void lookupByType(BeanFactory beanFactory) {
        User user = beanFactory.getBean(User.class);
        System.out.println("lookupByType: " + user);
    }

    private static void lookupInLazy(BeanFactory beanFactory) {
        ObjectFactory<User> objectFactory = (ObjectFactory<User>) beanFactory.getBean("objectFactory");
        User user = objectFactory.getObject();
        System.out.println("lookupInLazy: " + user);
    }

    private static void lookupByProvider(BeanFactory beanFactory) {
        ObjectProvider<User> objectProvider = beanFactory.getBeanProvider(User.class);
        User user = objectProvider.getObject();
        System.out.println("lookupByProvider: " + user);
    }

    private static void lookupByProviderIfAvailable(BeanFactory beanFactory) {
        ObjectProvider<String> beanProvider = beanFactory.getBeanProvider(String.class);
        String text = beanProvider.getIfAvailable(() -> "hello world");

        System.out.println("lookupByProviderIfAvailable: " + text);
    }

    private static void lookupByProviderStream(BeanFactory beanFactory) {
        ObjectProvider<User> beanProvider = beanFactory.getBeanProvider(User.class);
        beanProvider.stream().forEach(System.out::println);
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> userMap = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("lookupCollectionByType: " + userMap);
        }
    }

    private static void lookupByAnnotation(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, Object> userMap = listableBeanFactory.getBeansWithAnnotation(Super.class);
            System.out.println("lookupByAnnotation: " + userMap);
        }
    }
}
