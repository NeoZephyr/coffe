package com.pain.green.ioc.bean;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class BeanInitializationTests {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(Config.class);
        applicationContext.refresh();

        System.out.println("===== application refresh complete");

        UserFactory userFactory = applicationContext.getBean("userFactory", UserFactory.class);
        applicationContext.close();

        System.out.println("===== application close complete");
    }

    interface UserFactory {
        default User createUser() {
            User user = new User();
            user.setId(5L);
            user.setName("侯景");
            return user;
        }
    }

    static class DefaultUserFactory implements UserFactory, InitializingBean, DisposableBean {

        @PostConstruct
        public void init() {
            System.out.println("===== PostConstruct init");
        }

        public void init0() {
            System.out.println("===== initMethod init0");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("===== InitializingBean#afterPropertiesSet init");
        }

        @PreDestroy
        public void preDestroy() {
            System.out.println("===== PreDestroy destroy");
        }

        public void destroy0() {
            System.out.println("===== destroyMethod destroy0");
        }

        @Override
        public void destroy() throws Exception {
            System.out.println("===== DisposableBean#destroy destroy");
        }
    }


    static class Config {
        // initMethod destroyMethod 会设置到 BeanDefinition 里面
        @Bean(initMethod = "init0", destroyMethod = "destroy0")

        // 延迟初始化在 Refresh 之后进行
        // xml 里面是 lazy-init = "true"
        @Lazy(value = false)
        public UserFactory userFactory() {
            UserFactory userFactory = new DefaultUserFactory();
            return userFactory;
        }
    }
}
