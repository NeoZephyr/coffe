package com.pain.green.bean.definition;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 3. @Import 方式
 */
@Import(BeanDefinitionRegisterDemo.Config.class)
public class BeanDefinitionRegisterDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanDefinitionRegisterDemo.class);

        registerUserBeanDefinition(applicationContext, "another-user");
        registerUserBeanDefinition(applicationContext);

        // 外部单体对象注册
        SingletonBeanRegistry singletonBeanRegistry = applicationContext.getBeanFactory();
        User user = new User();
        user.setId(5L);
        user.setName("拓跋焘");
        singletonBeanRegistry.registerSingleton("single-user", user);

        applicationContext.refresh();

        System.out.println("config bean: " + applicationContext.getBeansOfType(Config.class));
        System.out.println("user bean: " + applicationContext.getBeansOfType(User.class));
        applicationContext.close();
    }

    private static void registerUserBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, String beanName) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("id", 3L)
                .addPropertyValue("name", "宇文泰");

        if (StringUtils.hasText(beanName)) {
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        } else {
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinitionBuilder.getBeanDefinition(), beanDefinitionRegistry);
        }
    }

    private static void registerUserBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry) {
        registerUserBeanDefinition(beanDefinitionRegistry, null);
    }

    /**
     * 2. @Component 方式
     */
    @Component
    public static class Config {

        /**
         * 1. @Bean 方式
         * @return
         */
        @Bean(name = {"user", "alias-user"})
        public User user() {
            User user = new User();
            user.setId(3L);
            user.setName("宇文泰");

            return user;
        }
    }
}
