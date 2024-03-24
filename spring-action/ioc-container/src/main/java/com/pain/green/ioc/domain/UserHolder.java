package com.pain.green.ioc.domain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class UserHolder implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware,
        InitializingBean, SmartInitializingSingleton, DisposableBean {
    private User user;
    private String desc;

    public UserHolder() {}

    public UserHolder(User user) {
        this.user = user;
    }

    public UserHolder(User user, String desc) {
        this.user = user;
        this.desc = desc;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UserHolder{" +
                "user=" + user +
                ", desc='" + desc + '\'' +
                ", beanClassLoader=" + beanClassLoader.getClass() +
                ", beanFactory=" + beanFactory.getClass() +
                ", beanName='" + beanName + '\'' +
                ", environment=" + environment +
                '}';
    }

    private ClassLoader beanClassLoader;
    private BeanFactory beanFactory;
    private String beanName;
    private Environment environment;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");
        this.desc = "user updated description v3";
    }

    @PostConstruct
    public void systemInit() {
        System.out.println("systemInit");
        this.desc = "user updated description v4";
    }

    public void customInit() {
        System.out.println("customInit");
        this.desc = "user updated description v5";
    }

    @PreDestroy
    public void systemDestroy() {
        System.out.println("systemDestroy");
        this.desc = "user updated description v9";
    }

    @Override
    public void afterSingletonsInstantiated() {
        System.out.println("afterSingletonsInstantiated");
        this.desc = "user updated description v7 ";
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy");
        this.desc = "user updated description v10";
    }

    public void customDestroy() {
        System.out.println("customDestroy");
        this.desc = "user updated description v11";
    }
}
