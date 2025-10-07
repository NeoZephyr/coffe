package com.pain.green.ioc.bean;

import com.pain.green.ioc.domain.User;
import com.pain.green.ioc.support.ThreadLocalScope;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.UUID;

public class BeanScopeTests {

    public static void main(String[] args) throws InterruptedException {
        // singletonAndPrototype();
        // threadLocalScopeTest();
    }

    private static void singletonAndPrototype() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(new DefaultListableBeanFactory());

        // autowired 注入属性解析
        // ctx.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());

        ctx.register(Config1.class);

        ctx.addBeanFactoryPostProcessor(beanFactory ->
                beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
                    @Override
                    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                        // 初始化回调
                        return bean;
                    }
        }));

        ctx.refresh();

        for (int i = 0; i < 3; i++) {
            User singletonUser = ctx.getBean("singletonUser", User.class);
            User prototypeUser = ctx.getBean("prototypeUser", User.class);
            System.out.println("singletonUser = " + singletonUser);
            System.out.println("prototypeUser = " + prototypeUser);
        }

        // prototype 每次查找、注入都生成新的
        // singleton 跟 prototype 都会执行初始化方法回调，比如 @PostConstruct
        // prototype 不会有完整的 bean 生命周期，比如 @PreDestroy 不会执行

        Config1 config1 = ctx.getBean(Config1.class);
        System.out.println("config1.singletonUser = " + config1.singletonUser);
        System.out.println("config1.prototypeUser = " + config1.prototypeUser);

        System.out.println("config1.anotherUser1 = " + config1.anotherUser1);
        System.out.println("config1.anotherUser2 = " + config1.anotherUser2);

        System.out.println("config1.users = " + config1.users);
    }

    // RequestScope/SessionScope/ServletContextScope
    // AbstractRequestAttributesScope#get

    private static void threadLocalScopeTest() throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config2.class);
        context.addBeanFactoryPostProcessor(beanFactory -> {
            beanFactory.registerScope(ThreadLocalScope.SCOPE_NAME, new ThreadLocalScope());
        });
        context.refresh();

        for (int i = 0; i < 3; ++i) {
            User user = context.getBean("user", User.class);
            System.out.printf("[Thread id: %d] bean: %s\n", Thread.currentThread().getId(), user);
        }

        for (int i = 0; i < 3; ++i) {
            Thread thread = new Thread(() -> {
                User user = context.getBean("user", User.class);
                System.out.printf("[Thread id: %d] bean: %s\n", Thread.currentThread().getId(), user);
            });

            thread.start();
            thread.join();
        }

        context.close();
    }

    static class Config1 implements DisposableBean {

        @Autowired
        private User singletonUser;

        @Autowired
        private User prototypeUser;

        @Autowired
        @Qualifier("singletonUser")
        private User anotherUser1;

        @Autowired
        @Qualifier("prototypeUser")
        private User anotherUser2;

        @Autowired
        private Map<String, User> users;

        @Autowired
        private ConfigurableListableBeanFactory beanFactory;

        @Bean
        public User singletonUser() {
            return new User(System.nanoTime(), "singleton");
        }

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public User prototypeUser() {
            return new User(System.nanoTime(), "prototype");
        }

        @Override
        public void destroy() throws Exception {
            // 清理非集合注入的 prototype bean

            for (Map.Entry<String, User> entry : users.entrySet()) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(entry.getKey());

                if (beanDefinition.isPrototype()) {
                    User user = entry.getValue();
                    // 清理集合注入的 prototype bean
                }
            }
        }
    }

    static class Config2 {
        @Bean
        @Scope(ThreadLocalScope.SCOPE_NAME)
        public User user() {
            return new User(System.nanoTime(), UUID.randomUUID().toString().substring(0, 5));
        }
    }
}
