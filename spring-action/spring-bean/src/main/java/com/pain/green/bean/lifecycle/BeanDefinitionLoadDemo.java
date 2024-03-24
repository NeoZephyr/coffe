package com.pain.green.bean.lifecycle;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

public class BeanDefinitionLoadDemo {
    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // loadByProperties(beanFactory);
        loadByAnnotation(beanFactory);
    }

    private static void loadByProperties(BeanFactory beanFactory) {
        PropertiesBeanDefinitionReader beanDefinitionReader = new PropertiesBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
        Resource resource = new ClassPathResource("META-INF/user.properties");
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        int beanDefinitionCount = beanDefinitionReader.loadBeanDefinitions(encodedResource);

        System.out.println("beanDefinitionCount: " + beanDefinitionCount);

        User user = beanFactory.getBean("user", User.class);
        System.out.println("user: " + user);
    }

    private static void loadByAnnotation(BeanFactory beanFactory) {
        AnnotatedBeanDefinitionReader beanDefinitionReader = new AnnotatedBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);

        int beanDefinitionCountBefore = ((BeanDefinitionRegistry) beanFactory).getBeanDefinitionCount();
        beanDefinitionReader.register(BeanDefinitionLoadDemo.class);
        int beanDefinitionCountAfter = ((BeanDefinitionRegistry) beanFactory).getBeanDefinitionCount();
        int beanDefinitionCount = beanDefinitionCountAfter - beanDefinitionCountBefore;

        System.out.println("beanDefinitionCount: " + beanDefinitionCount);
        BeanDefinitionLoadDemo demo = beanFactory.getBean("beanDefinitionLoadDemo", BeanDefinitionLoadDemo.class);

        System.out.println("demo: " + demo);
    }
}
