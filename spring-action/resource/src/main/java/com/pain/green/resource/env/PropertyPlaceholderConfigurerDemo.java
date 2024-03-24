package com.pain.green.resource.env;

import com.pain.green.ioc.domain.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PropertyPlaceholderConfigurerDemo {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("placeholders-resolver.xml");

        User user = applicationContext.getBean("user", User.class);

        System.out.println(user);

        applicationContext.close();
    }
}
