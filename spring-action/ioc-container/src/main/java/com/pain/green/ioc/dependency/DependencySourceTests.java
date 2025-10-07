package com.pain.green.ioc.dependency;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource(value = "META-INF/default.properties", encoding = "UTF-8")
public class DependencySourceTests {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private User user;

    /**
     * 依赖注入，能找到
     */
    @PostConstruct
    public void initByInjection() {
        System.out.println("beanFactory == applicationContext " + (beanFactory == applicationContext));
        System.out.println("beanFactory == applicationContext.getBeanFactory() " + (beanFactory == applicationContext.getAutowireCapableBeanFactory()));
        System.out.println("resourceLoader == applicationContext " + (resourceLoader == applicationContext));
        System.out.println("applicationEventPublisher == applicationContext " + (applicationEventPublisher == applicationContext));
    }

    /**
     * 依赖查找，不能找到
     */
    @PostConstruct
    public void initByLookup() {
        getBean(BeanFactory.class);
        getBean(ApplicationContext.class);
        getBean(ResourceLoader.class);
        getBean(ApplicationEventPublisher.class);
    }

    @PostConstruct
    public void resolvableDependency() {
        System.out.println("resolvableDependency: " + user);
    }

    @Value("${usr.id:-1}")
    private long userId;

    @Value("${usr.name:jack}")
    private String userName;

    @Value("${usr.resource:classpath://META-INF/default.properties}")
    private Resource resource;

    /**
     * 外部化配置注入
     */
    @PostConstruct
    public void init() {
        System.out.println("userId: " + userId);
        System.out.println("userName: " + userName);
        System.out.println("resource: " + resource);
    }

    private <T> T getBean(Class<T> beanType) {
        try {
            return beanFactory.getBean(beanType);
        } catch (NoSuchBeanDefinitionException ex) {
            System.err.println("bean type: " + beanType.getName() + " can not be found in beanFactory");
            return null;
        }
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(DependencySourceTests.class);

        applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
            // Resolvable Dependency 无法依赖查找，只能依赖注入
            beanFactory.registerResolvableDependency(User.class, new User(5L, "🐬"));
        });

        applicationContext.refresh();

        applicationContext.close();
    }
}
