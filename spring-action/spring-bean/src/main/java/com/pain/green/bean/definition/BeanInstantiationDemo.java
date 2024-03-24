package com.pain.green.bean.definition;

import com.pain.green.bean.factory.DefaultUserFactory;
import com.pain.green.bean.factory.UserFactory;
import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.serviceloader.ServiceLoaderFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Iterator;
import java.util.ServiceLoader;

public class BeanInstantiationDemo {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean-instantiation-context.xml");
        User userByStaticMethod = applicationContext.getBean("user-by-static-method", User.class);
        User userByInstanceMethod = applicationContext.getBean("user-by-instance-method", User.class);
        User userByFactoryBean = applicationContext.getBean("user-by-factory-bean", User.class);

        System.out.println("user by static method: " + userByStaticMethod);
        System.out.println("user by instance method: " + userByInstanceMethod);
        System.out.println("user by factory bean: " + userByFactoryBean);

        serviceLoaderDemo();

        ServiceLoader<UserFactory> userFactoryServiceLoader = applicationContext.getBean("userFactoryServiceLoader", ServiceLoader.class);
        listServiceLoader(userFactoryServiceLoader);

        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        UserFactory userFactory = autowireCapableBeanFactory.createBean(DefaultUserFactory.class);
        System.out.println(userFactory.createUser());
    }

    private static void serviceLoaderDemo() {
        ServiceLoader<UserFactory> serviceLoader = ServiceLoader.load(UserFactory.class, Thread.currentThread().getContextClassLoader());
        listServiceLoader(serviceLoader);
    }

    private static void listServiceLoader(ServiceLoader<UserFactory> serviceLoader) {
        Iterator<UserFactory> iterator = serviceLoader.iterator();

        while (iterator.hasNext()) {
            UserFactory userFactory = iterator.next();
            System.out.println(userFactory.createUser());
        }
    }
}
