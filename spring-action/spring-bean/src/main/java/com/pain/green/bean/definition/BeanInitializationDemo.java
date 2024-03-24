package com.pain.green.bean.definition;

import com.pain.green.bean.factory.DefaultUserFactory;
import com.pain.green.bean.factory.UserFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class BeanInitializationDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanInitializationDemo.class);
        applicationContext.refresh();

        System.out.println("application refresh complete");

        UserFactory userFactory = applicationContext.getBean("userFactory", UserFactory.class);
        applicationContext.close();

        System.out.println("application close complete");
    }

    @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
    @Lazy(value = false)
    public UserFactory userFactory() {
        UserFactory userFactory = new DefaultUserFactory();
        return userFactory;
    }
}
