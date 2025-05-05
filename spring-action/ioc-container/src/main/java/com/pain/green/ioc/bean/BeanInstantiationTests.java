package com.pain.green.ioc.bean;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ServiceLoader;

public class BeanInstantiationTests {

    public static void main(String[] args) {
        beanInstantiationTest();
    }

    private static void beanInstantiationTest() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean-instantiation-context.xml");
        User userByStaticMethod = applicationContext.getBean("user-by-static-method", User.class);
        User userByInstanceMethod = applicationContext.getBean("user-by-instance-method", User.class);
        User userByFactoryBean = applicationContext.getBean("user-by-factory-bean", User.class);

        System.out.println("user by static method: " + userByStaticMethod);
        System.out.println("user by instance method: " + userByInstanceMethod);
        System.out.println("user by factory bean: " + userByFactoryBean);

        ServiceLoader<UserFactory> serviceLoader = ServiceLoader.load(UserFactory.class, Thread.currentThread().getContextClassLoader());

        for (UserFactory userFactory : serviceLoader) {
            System.out.println("user by service load: " + userFactory.createUser());
        }

        ServiceLoader<UserFactory> userFactoryServiceLoader = applicationContext.getBean("userFactoryServiceLoader", ServiceLoader.class);

        for (UserFactory userFactory : userFactoryServiceLoader) {
            System.out.println("user by service load bean:" + userFactory.createUser());
        }

        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        UserFactory userFactory = autowireCapableBeanFactory.createBean(DefaultUserFactory.class);
        System.out.println("user by AutowireCapableBeanFactory createBean: " + userFactory.createUser());
    }

    public static class UserFactoryBean implements FactoryBean<User> {
        @Override
        public User getObject() throws Exception {
            return User.createUser();
        }

        @Override
        public Class<?> getObjectType() {
            return User.class;
        }
    }


    public interface UserFactory {
        default User createUser() {
            User user = new User();
            user.setId(5L);
            user.setName("侯景");
            return user;
        }
    }

    public static class DefaultUserFactory implements UserFactory {}
}
