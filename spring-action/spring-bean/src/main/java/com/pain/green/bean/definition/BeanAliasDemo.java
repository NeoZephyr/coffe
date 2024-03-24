package com.pain.green.bean.definition;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanAliasDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean-definitions-context.xml");
        User user = beanFactory.getBean("user", User.class);
        User anotherUser = beanFactory.getBean("anotherUser", User.class);

        System.out.println("user == anotherUser, " + (user == anotherUser));
    }
}
