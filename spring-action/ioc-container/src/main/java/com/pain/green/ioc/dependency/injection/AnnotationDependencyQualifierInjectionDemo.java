package com.pain.green.ioc.dependency.injection;

import com.pain.green.ioc.annotation.ChinaUser;
import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Collection;

public class AnnotationDependencyQualifierInjectionDemo {

    @Autowired
    private User user;

    @Autowired
    @Qualifier("user")
    private User namedUser;

    @Autowired
    private Collection<User> allUser;

    @Autowired
    @Qualifier
    private Collection<User> qualifiedUsers;

    @Autowired
    @ChinaUser
    private Collection<User> chinaUser;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationDependencyQualifierInjectionDemo.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        AnnotationDependencyQualifierInjectionDemo demo = applicationContext.getBean(AnnotationDependencyQualifierInjectionDemo.class);

        System.out.println("user: " + demo.user);
        System.out.println("namedUser: " + demo.namedUser);
        System.out.println("allUser: " + demo.allUser);
        System.out.println("qualifiedUsers: " + demo.qualifiedUsers);
        System.out.println("chinaUser: " + demo.chinaUser);

        applicationContext.close();
    }

    @Bean
    @Qualifier
    public User googleUser() {
        return new User(6L, "安卓");
    }

    @Bean
    @Qualifier
    public User appleUser() {
        return new User(7L, "iPhone 手机");
    }

    @Bean
    @ChinaUser
    public User wechatUser() {
        return new User(8L, "马化腾");
    }

    @Bean
    @ChinaUser
    public User aliUser() {
        return new User(9L, "马云");
    }
}
