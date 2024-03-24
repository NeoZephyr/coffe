package com.pain.green.ioc.dependency.injection.setter;

import com.pain.green.ioc.domain.UserHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class ApiDependencySetterInjectionDemo {
    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserHolder.class);
        beanDefinitionBuilder.addPropertyReference("user", "superUser");

        beanFactory.registerBeanDefinition("userHolder", beanDefinitionBuilder.getBeanDefinition());

        UserHolder userHolder = beanFactory.getBean(UserHolder.class);

        System.out.println("userHolder: " + userHolder);
    }
}
