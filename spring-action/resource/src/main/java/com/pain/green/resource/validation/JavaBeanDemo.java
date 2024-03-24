package com.pain.green.resource.validation;

import com.pain.green.ioc.domain.User;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.stream.Stream;

public class JavaBeanDemo {
    public static void main(String[] args) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(User.class, Object.class);

        Stream.of(beanInfo.getPropertyDescriptors()).forEach(System.out::println);
        Stream.of(beanInfo.getMethodDescriptors()).forEach(System.out::println);
    }
}
