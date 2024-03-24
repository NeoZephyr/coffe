package com.pain.green.ioc.dependency.injection.setter;

import com.pain.green.ioc.domain.UserHolder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlDependencySetterInjectionDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:dependency-setter-injection.xml");
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println("userHolder: " + userHolder);
    }
}
