package com.pain.green.ioc.container;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class HierarchicalBeanFactoryDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // applicationContext.register(AnnotationApplicationContextDemo.class);
        applicationContext.refresh();

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();

        System.out.println("parentBeanFactory: " + parentBeanFactory);

        parentBeanFactory = createParentBeanFactory();
        beanFactory.setParentBeanFactory(parentBeanFactory);

        displayContainsLocalBean(beanFactory, "superUser");
        displayContainsLocalBean((HierarchicalBeanFactory) parentBeanFactory, "superUser");

        displayContainsBean(beanFactory, "superUser");
        displayContainsBean((HierarchicalBeanFactory) parentBeanFactory, "superUser");

        applicationContext.close();
    }

    private static void displayContainsLocalBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("%s 是否包含 bean %s: %s\n", beanFactory,  beanName, beanFactory.containsLocalBean(beanName));
    }

    private static void displayContainsBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("%s 是否包含 bean %s: %s\n", beanFactory,  beanName, containsBean(beanFactory, beanName));
    }

    private static boolean containsBean(HierarchicalBeanFactory beanFactory, String beanName) {
        BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();

        if (parentBeanFactory instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hierarchicalBeanFactory = (HierarchicalBeanFactory) parentBeanFactory;

            if (containsBean(hierarchicalBeanFactory, beanName)) {
                return true;
            }
        }

        return beanFactory.containsLocalBean(beanName);
    }

    private static BeanFactory createParentBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        String location = "classpath:dependency-lookup-context.xml";
        beanDefinitionReader.loadBeanDefinitions(location);

        return beanFactory;
    }
}