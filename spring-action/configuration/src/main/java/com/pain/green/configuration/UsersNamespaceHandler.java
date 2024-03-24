package com.pain.green.configuration;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class UsersNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("user", new UserBeanDefinitionParser());
    }
}
