package com.pain.green.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;

public class InjectResourceLoaderDemo implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Autowired
    private ResourceLoader autowiredResourceLoader;

    @Autowired
    private AbstractApplicationContext abstractApplicationContext;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        System.out.println("resourceLoader == autowiredResourceLoader, " + (resourceLoader == autowiredResourceLoader));
        System.out.println("resourceLoader == abstractApplicationContext, " + (resourceLoader == abstractApplicationContext));
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(InjectResourceLoaderDemo.class);
        applicationContext.refresh();
        applicationContext.close();
    }
}
