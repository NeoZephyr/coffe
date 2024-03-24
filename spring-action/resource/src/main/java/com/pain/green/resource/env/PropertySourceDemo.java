package com.pain.green.resource.env;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

public class PropertySourceDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(LookupEnvironmentDemo.class);
        applicationContext.refresh();
        StandardEnvironment env = (StandardEnvironment) applicationContext.getBean(ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME, Environment.class);
        env.getPropertySources().forEach(propertySource -> {
            System.out.printf("%s : %s\n", propertySource.getName(), propertySource.getProperty("user.name"));
        });
    }
}
