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
        // 依赖来源一：自定义 bean
        UserRepo userRepo = (UserRepo) beanFactory.getBean("autoUserRepo");

        // 依赖来源二：依赖注入，内建依赖（非 Bean）
        // ClassPathXmlApplicationContext -> AbstractXmlApplicationContext -> AbstractRefreshableConfigApplicationContext
        //  -> AbstractRefreshableApplicationContext -> AbstractApplicationContext -> ConfigurableApplicationContext
        //  -> ApplicationContext -> ListableBeanFactory -> BeanFactory
        //
        // AbstractRefreshableApplicationContext 虽然 implement 了 BeanFactory，但是是通过内部又持有了一个 BeanFactory 对象来实现的
        // AbstractRefreshableApplicationContext#getBeanFactory

        // 真正的 IoC 的底层实现就是 BeanFactory 的实现类，ApplicationContext 委托 DefaultListableBeanFactory 来操作 getBean 等方法

        // 为什么 UserRepo 的 beanFactory 字段注入的是 DefaultListableBeanFactory，而不是 ClassPathXmlApplicationContext
        // 既然他们两者都实现了 BeanFactory 接口，按 type 注入，为什么不是 ClassPathXmlApplicationContext？

        // AbstractApplicationContext#prepareBeanFactory 方法中，beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        // 明确地指定了 BeanFactory 类型的对象是 ApplicationContext#getBeanFactory() 方法的内容，而非它自身
        BeanFactory injectBeanFactory = userRepo.getBeanFactory();
        System.out.println("injectBeanFactory: " + injectBeanFactory);
        System.out.println("beanFactory == injectBeanFactory, " + (beanFactory == injectBeanFactory));

        // 依赖来源三：内建 bean
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
