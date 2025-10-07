package com.pain.green.ioc.bean;

import com.pain.green.ioc.domain.SuperUser;
import com.pain.green.ioc.domain.User;
import com.pain.green.ioc.domain.UserHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.ObjectUtils;

import java.util.ServiceLoader;

public class BeanInstantiationTests {

    public static void main(String[] args) {
        // beanInstantiationTest();

        instantiationAwareBeanPostProcessorTest();
    }

    private static void beanInstantiationTest() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean-instantiation-context.xml");
        User userByStaticMethod = applicationContext.getBean("user-by-static-method", User.class);
        User userByInstanceMethod = applicationContext.getBean("user-by-instance-method", User.class);
        User userByFactoryBean = applicationContext.getBean("user-by-factory-bean", User.class);

        System.out.println("user by static method: " + userByStaticMethod);
        System.out.println("user by instance method: " + userByInstanceMethod);
        System.out.println("user by factory bean: " + userByFactoryBean);

        ServiceLoader<UserFactory> serviceLoader = ServiceLoader.load(UserFactory.class, Thread.currentThread().getContextClassLoader());

        for (UserFactory userFactory : serviceLoader) {
            System.out.println("user by service load: " + userFactory.createUser());
        }

        ServiceLoader<UserFactory> userFactoryServiceLoader = applicationContext.getBean("userFactoryServiceLoader", ServiceLoader.class);

        for (UserFactory userFactory : userFactoryServiceLoader) {
            System.out.println("user by service load bean:" + userFactory.createUser());
        }

        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        UserFactory userFactory = autowireCapableBeanFactory.createBean(DefaultUserFactory.class);
        System.out.println("user by AutowireCapableBeanFactory createBean: " + userFactory.createUser());
    }

    private static void instantiationAwareBeanPostProcessorTest() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 处理 @PostConstruct
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());

        beanFactory.addBeanPostProcessor(new UserInstantiationAwareBeanPostProcessor());
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        String location = "bean-definitions-context.xml";
        Resource resource = new ClassPathResource(location);
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        beanDefinitionReader.loadBeanDefinitions(encodedResource);

        // SmartInitializingSingleton 通常在 Spring ApplicationContext 场景使用
        // preInstantiateSingletons 将已注册的 BeanDefinition 初始化成 Spring Bean
        beanFactory.preInstantiateSingletons();

        User user = beanFactory.getBean("user", User.class);
        System.out.println("user: " + user);

        User superUser = beanFactory.getBean("superUser", User.class);
        System.out.println("superUser: " + superUser);

        // 根据类型还是名称注入？
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
        System.out.println("userHolder: " + userHolder);

        beanFactory.destroyBean("userHolder", userHolder);

        System.out.println("userHolder: " + userHolder);
    }

    /**
     * ApplicationContextAwareProcessor 只有 ApplicationContext 才能使用
     */
    private static void applicationContextTest() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        String location = "bean-definitions-context.xml";
        applicationContext.setConfigLocations(location);

        // refresh 方法里面有一个 registerBeanPostProcessors 调用
        // 讲 xml 文件中配置的 UserInstantiationAwareBeanPostProcessor 添加到了 beanPostProcessors 列表里面
        applicationContext.refresh();

        User user = applicationContext.getBean("user", User.class);
        System.out.println("user: " + user);

        User superUser = applicationContext.getBean("superUser", User.class);
        System.out.println("superUser: " + superUser);

        UserHolder userHolder = applicationContext.getBean("userHolder", UserHolder.class);
        System.out.println("userHolder: " + userHolder);

        applicationContext.close();
    }

    public static class UserFactoryBean implements FactoryBean<User> {
        @Override
        public User getObject() throws Exception {
            return User.createUser();
        }

        @Override
        public Class<?> getObjectType() {
            return User.class;
        }
    }


    public interface UserFactory {
        default User createUser() {
            User user = new User();
            user.setId(5L);
            user.setName("侯景");
            return user;
        }
    }

    public static class DefaultUserFactory implements UserFactory {}

    public static class UserInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor, DestructionAwareBeanPostProcessor {

        // AbstractAutowireCapableBeanFactory#createBean
        //     AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation
        //         AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation
        //         AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization
        //     AbstractAutowireCapableBeanFactory#doCreateBean
        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {

            // 当返回非空的时候，后续的 doCreateBean 都不做了
            if (ObjectUtils.nullSafeEquals("superUser", beanName) && SuperUser.class.equals(beanClass)) {
                return new SuperUser();
            }

            // 保持 Spring IOC 容器的实例化操作
            return null;
        }

        /**
         * 控制 bean 属性要不要进行赋值
         */
        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("user", beanName) && User.class.equals(bean.getClass())) {
                User user = (User) bean;
                user.setId(12L);
                user.setName("王安石");

                // 不允许属性赋值（填入）（配置元信息 -> 属性值）
                return false;
            }

            // 默认为 true
            return true;
        }

        /**
         * 赋值前，对属性进行修改
         */
        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
                MutablePropertyValues propertyValues;

                if (pvs instanceof MutablePropertyValues) {
                    propertyValues = (MutablePropertyValues) pvs;
                } else {
                    propertyValues = new MutablePropertyValues();
                }

                if (propertyValues.contains("desc")) {
                    propertyValues.removePropertyValue("desc");
                    propertyValues.addPropertyValue("desc", "user updated descriptionV1");
                }

                return propertyValues;
            }

            // 对属性没有修改
            return null;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
                UserHolder userHolder = (UserHolder) bean;
                userHolder.setDesc("user updated description v2");
            }

            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
                UserHolder userHolder = (UserHolder) bean;
                userHolder.setDesc("user updated description v6");
            }

            return bean;
        }

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
                UserHolder userHolder = (UserHolder) bean;
                userHolder.setDesc("user updated description v8");
            }
        }
    }
}
