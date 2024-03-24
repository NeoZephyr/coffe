package com.pain.green.ioc.dependency.injection.constructor;

import com.pain.green.ioc.domain.UserHolder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlDependencyConstructorInjectionDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:dependency-constructor-injection.xml");
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println("userHolder: " + userHolder);
    }
}
