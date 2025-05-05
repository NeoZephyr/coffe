package com.pain.green.bean;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanDemo {
    public static void main(String[] args) {
        beanAliasTest();
    }

    private static void beanAliasTest() {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean-definitions-context.xml");
        User user = beanFactory.getBean("user", User.class);
        User alias = beanFactory.getBean("anotherUser", User.class);

        System.out.println("(user == alias user): " + (user == alias));
    }
}
