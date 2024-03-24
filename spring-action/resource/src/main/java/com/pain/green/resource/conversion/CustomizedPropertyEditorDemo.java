package com.pain.green.resource.conversion;

import com.pain.green.ioc.domain.User;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CustomizedPropertyEditorDemo {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/beans.xml");

        User user = applicationContext.getBean(User.class);

        System.out.println(user);

        applicationContext.close();
    }
}
