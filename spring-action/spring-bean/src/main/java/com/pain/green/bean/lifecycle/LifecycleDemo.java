package com.pain.green.bean.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class LifecycleDemo {

    public static void main(String[] args) {
        autowiredFailTest();
    }

    private static void lifecycleTest() {
        // AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(new DefaultListableBeanFactory());
        ctx.register(Config.class);

        // autowired 注入属性解析
        // ctx.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        ctx.refresh();

        ctx.close();
    }

    // Aware 接口用于注入一些与容器相关的信息，例如
    // 1. BeanNameAware 注入 Bean 名称
    // 2. BeanFactoryAware 注入 BeanFactory 容器
    // 3. ApplicationContextAware 注入 ApplicationContext 容器
    // 4. EmbeddedValueResolverAware 注入，用于解析 ${}

    // 2、3、4 用 @Autowired 也能实现，但是
    // @Autowired 解析需要用到 BeanPostProcessor，用于扩展功能。某些情况扩展功能会失效
    // Aware 接口属于内置功能，不需要扩展

    private static void autowiredFailTest() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        context.registerBean(ConfigurationClassPostProcessor.class);

        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());

        // 1. 找到 BeanFactoryPostProcessor 执行，补充 BeanDefinition
        // 2. 添加 BeanPostProcessor
        // 3. 初始化单列
        context.refresh();

        context.close();
    }

    @Configuration
    static class Config {
        @Bean
        MyBean myBean() {
            return new MyBean();
        }

        @Autowired
        public void setApplicationContext(ApplicationContext context) {
            System.out.println("@Autowired Config#setApplicationContext");
        }

        @PostConstruct
        public void init() {
            System.out.println("@PostConstruct Config#init()");
        }

        // 加了 BeanFactoryPostProcessor 之后，发现上面的 @Autowired 与 @PostConstruct 都失效了
        // 分析，正常流程是
        // 1. 执行 BeanFactoryPostProcessor
        // 2. 注册 BeanPostProcessor
        // 3. 创建和初始化 Bean
        //    1. 依赖注入和扩展 @Value @Autowired
        //    2. 初始化扩展 @PostConstruct
        //    3. 执行 Aware 及 InitializingBean

        // 由于提供的 BeanFactoryPostProcessor 是通过工厂方法的模式提供的，在调用的时候需要先对 Config 实例化，才能执行工厂方法
        // 所有先创建了配置类 Config 对象，此时是没有加 BeanPostProcessor 的，就只能执行 Config 类里面内置的接口如 Aware 和 InitializingBean
        // 至于扩展的 @Autowired @PostConstruct 是没法处理的，于是失效了

        // 1. 创建和初始化 Config
        // 2. 执行 Config 的 Aware 及 InitializingBean
        // 3. 执行 BeanFactoryPostProcessor
        // 4. 注册 BeanPostProcessor

        // @Bean
        BeanFactoryPostProcessor beanFactoryPostProcessor() {
            return beanFactory -> {
                System.out.println("<<<<< 执行 postProcessBeanFactory");
            };
        }

        @Bean
        MyBeanPostProcessor myBeanPostProcessor() {
            return new MyBeanPostProcessor();
        }
    }

    static class MyBean {

        // 三种初始化方法执行顺序
        // @PostConstruct -> InitializingBean -> @Bean 指定的 init 方法

        // 三种销毁方法执行顺序
        // @PreDestroy -> DisposableBean -> @Bean 指定的 destory 方法

        public MyBean() {
            System.out.println("construct invoked");
        }

        @Autowired
        public void autowire(@Value("${SPARK_HOME}") String home) {
            // 这里没有能读取到环境变量
            // 可能时候 shell 的原因
            System.out.println("autowired env home: " + home);
        }

        @PostConstruct
        public void init() {
            System.out.println("@PostConstruct MyBean#init()");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("@PreDestroy MyBean#destroy()");
        }
    }

    static class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, DestructionAwareBeanPostProcessor {

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 销毁之前执行，比如 @PreDestroy");
            }
        }

        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 实例化之前执行，这里返回的对象会替换原本的 bean");
            }

            return null;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 实例化之后执行，如果返回 false 会替换掉原本的 bean");
            }

            return true;
        }

        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 依赖注入阶段执行，比如 @Autowired、@Value、@Resource");
            }

            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 初始化之前执行，这里返回的对象会替换掉原本的 bean，如 @PostConstruct、@ConfigurationProperties");
            }

            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (beanName.equals("myBean")) {
                System.out.println("<<<<< 初始化之后执行，这里返回的对象会替换掉原本的 bean，如代理增强");
            }

            return bean;
        }
    }
}
