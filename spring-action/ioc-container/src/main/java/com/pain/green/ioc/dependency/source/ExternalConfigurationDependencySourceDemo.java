package com.pain.green.ioc.dependency.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource(value = "META-INF/default.properties", encoding = "UTF-8")
public class ExternalConfigurationDependencySourceDemo {

    @Value("${usr.id:-1}")
    private long userId;

    @Value("${usr.name:jack}")
    private String userName;

    @Value("${usr.resource:classpath://META-INF/default.properties}")
    private Resource resource;

    @PostConstruct
    public void init() {
        System.out.println("userId: " + userId);
        System.out.println("userName: " + userName);
        System.out.println("resource: " + resource);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ExternalConfigurationDependencySourceDemo.class);
        applicationContext.refresh();

        applicationContext.close();
    }
}
