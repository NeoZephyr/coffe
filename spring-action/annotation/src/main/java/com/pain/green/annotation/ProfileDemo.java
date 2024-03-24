package com.pain.green.annotation;

import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class ProfileDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ProfileDemo.class);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        environment.setDefaultProfiles("even");
        environment.setActiveProfiles("odd");
        applicationContext.refresh();

        Integer number = applicationContext.getBean("number", Integer.class);

        System.out.println(number);

        applicationContext.close();
    }

    @Bean("number")
    @Profile("odd")
    public Integer odd() {
        return 1;
    }

    @Bean("number")
    @Conditional(EvenProfileCondition.class)
    public Integer even() {
        return 2;
    }
}
