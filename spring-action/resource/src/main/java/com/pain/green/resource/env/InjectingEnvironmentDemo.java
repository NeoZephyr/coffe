package com.pain.green.resource.env;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

public class InjectingEnvironmentDemo implements EnvironmentAware, ApplicationContextAware {

    private Environment environment1;

    @Autowired
    private Environment environment2;

    private ApplicationContext applicationContext1;

    @Autowired
    private ApplicationContext applicationContext2;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(InjectingEnvironmentDemo.class);
        applicationContext.refresh();

        InjectingEnvironmentDemo injectingEnvironmentDemo = applicationContext.getBean(InjectingEnvironmentDemo.class);

        System.out.println(injectingEnvironmentDemo.environment1 == injectingEnvironmentDemo.environment2);
        System.out.println(injectingEnvironmentDemo.applicationContext1 == injectingEnvironmentDemo.applicationContext2);
        System.out.println(injectingEnvironmentDemo.environment1 == injectingEnvironmentDemo.applicationContext1.getEnvironment());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment1 = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext1 = applicationContext;
    }
}
