package com.pain.green.ioc.dependency.injection.setter;

import com.pain.green.ioc.domain.UserHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class AutoWiringDependencySetterInjectionDemo {
    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("dependency-autowire-setter-injection.xml");

        UserHolder userHolder = beanFactory.getBean(UserHolder.class);

        System.out.println("userHolder: " + userHolder);
    }
}
