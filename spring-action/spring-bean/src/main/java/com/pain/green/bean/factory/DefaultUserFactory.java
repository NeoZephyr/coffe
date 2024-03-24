package com.pain.green.bean.factory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class DefaultUserFactory implements UserFactory, InitializingBean, DisposableBean {

    @PostConstruct
    public void init() {
        System.out.println("PostConstruct init");
    }

    public void customInit() {
        System.out.println("initMethod init");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean#afterPropertiesSet init");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("PreDestroy destroy");
    }

    public void customDestroy() {
        System.out.println("destroyMethod destroy");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean#destroy destroy");
    }
}
