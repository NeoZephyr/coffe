package com.pain.green.bean.definition;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class BeanDefinitionCreateDemo {
    public static void main(String[] args) {
        createBeanDefinitionByBuilder();
        createBeanDefinitionByNew();
    }

    private static void createBeanDefinitionByBuilder() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("id", 1)
                .addPropertyValue("name", "尔朱荣");
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        System.out.println("BeanDefinition: " + beanDefinition);
    }

    private static void createBeanDefinitionByNew() {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(User.class);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("id", 2)
                .add("name", "高欢");
        beanDefinition.setPropertyValues(propertyValues);

        System.out.println("beanDefinition: " + beanDefinition);
    }
}
