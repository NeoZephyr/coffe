package com.pain.green.bean.lifecycle;

import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.reflect.Method;

public class BeanPostProcessorDemo {

    public static void main(String[] args) throws Throwable {
        autowiredAnnotationBeanPostProcessorTest();
    }

    private static void beanPostProcessorTest() {
        GenericApplicationContext ctx = new GenericApplicationContext();

        ctx.registerBean("a", A.class);
        ctx.registerBean("b", B.class);
        ctx.registerBean("c", C.class);
        ctx.registerBean("d", D.class);

        ctx.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver()); // @Value
        ctx.registerBean(AutowiredAnnotationBeanPostProcessor.class); // @Autowired @Value
        ctx.registerBean(CommonAnnotationBeanPostProcessor.class); // @Resource @PostConstruct @PreDestroy

        ConfigurationPropertiesBindingPostProcessor.register(ctx.getDefaultListableBeanFactory()); // @ConfigurationProperties

        ctx.refresh();

        D d = ctx.getBean(D.class);

        System.out.println("bean d: " + d);

        ctx.close();
    }

    private static void autowiredAnnotationBeanPostProcessorTest() throws Throwable {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("b", new B());
        beanFactory.registerSingleton("c", new C());
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders); // ${} 解析器

        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);

        A a = new A();

        // 1. 查找哪些属性、方法加了 @Autowired
        Method method = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod(
                "findAutowiringMetadata", String.class, Class.class, PropertyValues.class);
        method.setAccessible(true);

        // injectedElements
        InjectionMetadata metadata = (InjectionMetadata) method.invoke(bpp, "a", A.class, null);

        // 2. 调用 InjectionMetadata 来进行依赖注入，注入时按类型查找值
        // AutowiredAnnotationBeanPostProcessor#postProcessProperties 方法
        metadata.inject(a, "a", null);
        System.out.println(a);
    }

    static class A {
        private B b;
        private C c;
        private String user;

        @Resource
        public void setB(B b) {
            System.out.println("@Resource " + b);
            this.b = b;
        }

        @Autowired
        public void setC(C c) {
            System.out.println("@Autowired " + c);
            this.c = c;
        }

        @Autowired
        public void setUser(@Value("${USER}") String user) {
            System.out.println("autowired user " + user);
            this.user = user;
        }

        @PostConstruct
        public void init() {
            System.out.println("@PostConstruct 生效");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("@PreDestroy 生效");
        }

        @Override
        public String toString() {
            return "A{" +
                    "b=" + b +
                    ", c=" + c +
                    ", user='" + user + '\'' +
                    '}';
        }
    }

    static class B {}

    static class C {}

    @ConfigurationProperties(prefix = "java")
    static class D {
        private String home;
        private String version;

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return "D{" +
                    "home='" + home + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }
}
