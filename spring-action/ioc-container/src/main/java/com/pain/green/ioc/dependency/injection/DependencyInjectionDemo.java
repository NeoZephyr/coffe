package com.pain.green.ioc.dependency.injection;

import com.pain.green.ioc.domain.UserRepo;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

public class DependencyInjectionDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:dependency-injection-context.xml");

        manualInject(beanFactory);
        autoInject(beanFactory);
        builtInInject(beanFactory);
        lazyInject(beanFactory);

        System.out.println(beanFactory);

        // ApplicationContext 实现 BeanFactory 接口，同时包含一个 BeanFactory 对象
        // Not exists
        // System.out.println(beanFactory.getBean(BeanFactory.class));
    }

    private static void manualInject(BeanFactory beanFactory) {
        UserRepo userRepo = (UserRepo) beanFactory.getBean("manualUserRepo");
        System.out.println("manualInject: " + userRepo.getUsers());
    }

    private static void autoInject(BeanFactory beanFactory) {
        UserRepo userRepo = (UserRepo) beanFactory.getBean("autoUserRepo");
        System.out.println("autoUserRepo: " + userRepo.getUsers());
    }

    private static void builtInInject(BeanFactory beanFactory) {
        // 自定义 bean
        UserRepo userRepo = (UserRepo) beanFactory.getBean("autoUserRepo");

        // 依赖注入，内建依赖
        BeanFactory injectBeanFactory = userRepo.getBeanFactory();
        System.out.println("injectBeanFactory: " + injectBeanFactory);
        System.out.println("beanFactory == injectBeanFactory, " + (beanFactory == injectBeanFactory));

        // 内建 bean
        Environment environment = beanFactory.getBean(Environment.class);
        System.out.println("environment: " + environment);
    }

    private static void lazyInject(BeanFactory beanFactory) {
        UserRepo userRepo = (UserRepo) beanFactory.getBean("autoUserRepo");
        ObjectFactory objectFactory = userRepo.getObjectFactory();
        BeanFactory lazyBeanFactory = (BeanFactory) objectFactory.getObject();
        System.out.println("lazyBeanFactory: " + lazyBeanFactory);
        System.out.println("beanFactory == lazyBeanFactory, " + (beanFactory == lazyBeanFactory));
    }
}
