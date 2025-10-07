package com.pain.green.ioc.bean;

import com.pain.green.ioc.annotation.Super;
import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class BeanLookupTests {

    public static void main(String[] args) {
        // lookup();
        // lookupLazy();
        lookupHierarchical();
    }

    private static void lookup() {
        ListableBeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:bean-definitions-context.xml");
        User user = (User) beanFactory.getBean("user");
        System.out.println("lookupById, user: " + user);

        user = beanFactory.getBean(User.class);
        System.out.println("lookupByType, user: " + user);

        Map<String, Object> beans = beanFactory.getBeansWithAnnotation(Super.class);
        System.out.println("lookupByAnnotation, beans: " + beans);

        // 安全依赖查找
        Map<String, User> users = beanFactory.getBeansOfType(User.class);
        System.out.println("lookupCollectionByType, users: " + users);

        user = beanFactory.getBean("user", User.class);
        User alias = beanFactory.getBean("anotherUser", User.class);

        System.out.println("lookupByAlias, (user == alias user): " + (user == alias));
    }

    private static void lookupLazy() {
        ListableBeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:bean-definitions-context.xml");

        ObjectFactory<User> objectFactory = (ObjectFactory<User>) beanFactory.getBean("objectFactory");
        User user = objectFactory.getObject();
        System.out.println("lookupInLazy: " + user);

        // ObjectProvider is ObjectFactory
        ObjectProvider<User> beanProvider1 = beanFactory.getBeanProvider(User.class);
        user = beanProvider1.getObject();
        System.out.println("lookupByProvider: " + user);

        // 安全依赖查找
        ObjectProvider<String> beanProvider2 = beanFactory.getBeanProvider(String.class);
        String text = beanProvider2.getIfAvailable(() -> "hello world");

        System.out.println("lookupByProvider with fallback: " + text);

        // 安全依赖查找
        ObjectProvider<User> beanProvider3 = beanFactory.getBeanProvider(User.class);
        beanProvider3.stream().forEach(bean -> {
            System.out.println("lookupByProvider stream: " + bean);
        });
    }

    private static void lookupHierarchical() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.refresh();

        // Listable, Configurable, Hierarchical
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

    private static BeanFactory createParentBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        String location = "classpath:dependency-lookup-context.xml";
        beanDefinitionReader.loadBeanDefinitions(location);

        return beanFactory;
    }

    private static void displayContainsLocalBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("%s 是否包含 bean %s: %s\n", beanFactory,  beanName, beanFactory.containsLocalBean(beanName));
    }

    private static void displayContainsBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("%s 是否包含 bean %s: %s\n", beanFactory,  beanName, containsBean(beanFactory, beanName));
    }

    /**
     * 层次性依赖查找
     */
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
}
