package com.pain.green.ioc.dependency.injection;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Set;

public class AnnotationDependencyLazyInjectionDemo {

    @Autowired
    private ObjectFactory<Set<User>> objectFactory;

    @Autowired
    private ObjectProvider<User> objectProvider;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationDependencyLazyInjectionDemo.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        AnnotationDependencyLazyInjectionDemo demo = applicationContext.getBean(AnnotationDependencyLazyInjectionDemo.class);

        System.out.println("objectFactory: " + demo.objectFactory.getObject());
        System.out.println("objectProvider: " + demo.objectProvider.getObject());

        demo.objectProvider.forEach(System.out::println);

        applicationContext.close();
    }

    @Bean
    public User user() {
        return new User(10L, "石虎");
    }
}
