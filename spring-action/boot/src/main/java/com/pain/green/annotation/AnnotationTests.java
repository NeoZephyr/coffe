package com.pain.green.annotation;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

public class AnnotationTests {

    public static void componentScanTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(FooConfig.class);
        applicationContext.refresh();

        Foo foo = applicationContext.getBean(Foo.class);

        System.out.println(foo);

        applicationContext.close();
    }

    public static void importTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // applicationContext.register(MasterConfig.class);
        applicationContext.register(FooModule.class);

        // 不允许 Bean 定义覆盖，默认为 true
        applicationContext.getDefaultListableBeanFactory().setAllowBeanDefinitionOverriding(false);
        applicationContext.refresh();

        System.out.println(applicationContext.getBean(Bean1.class));
        System.out.println(applicationContext.getBean(Bean2.class));

        // System.out.println(applicationContext.getBean("foo"));

        applicationContext.close();
    }

    public static void profileTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ProfileConfig.class);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        environment.setDefaultProfiles("local");
        environment.setActiveProfiles("dev");
        applicationContext.refresh();

        Setting setting = applicationContext.getBean("setting", Setting.class);

        System.out.println(setting);
        applicationContext.close();
    }

    static class ProfileConfig {

        @Bean("setting")
        @Profile("local")
        public Setting local() {
            return new LocalSetting();
        }

        @Bean("setting")
        @Conditional(DevProfileCondition.class)
        public Setting dev() {
            return new DevSetting();
        }
    }

    static class DevProfileCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

            // 参考 ProfileCondition
            Environment environment = context.getEnvironment();
            return environment.acceptsProfiles(Profiles.of("dev"));
        }
    }

    @FooComponentScan(scanBasePackages = "com.pain.green.annotation")
    public static class FooConfig {}

    @FooComponent
    public static class Foo {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Component
    public @interface FooComponent {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @ComponentScan
    public @interface FooComponentScan {

        // 子注解提供新的属性方法引用父（元）注解中的属性方法
        @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
        String[] scanBasePackages() default {};

        // 覆盖了元注解 basePackages
        @AliasFor("basePackages")
        String[] packages() default {};

        // 与元注解 @ComponentScan 同名属性
        String[] basePackages() default {};
    }

    @Configuration
    // @Import(Bean1.class) // 直接引入单个 Bean
    // @Import(Config0.class) // 引入一个配置类
    // @Import(Registry.class) // 通过 BeanDefinition 注册器
    // @Import(Selector.class) // Selector 引入多个类，Selector 本身不引入
    static class ImportConfig {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    // @Import(Config0.class)
    // @Import(Selector.class)
    @Import(Registry.class)
    public @interface EnableFoo {}

    @EnableFoo
    public static class FooModule {}

    @Configuration
    static class Config0 {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }
    }

    @Configuration
    // @Import(SlaveConfig.class)
    @Import(DeferredSelector.class)
    static class MasterConfig { // 主配置
        @Bean
        public Bean1 foo() {
            return new Bean1();
        }
    }

    @Configuration
    static class SlaveConfig { // 从配置 - 自动配置（优先级应该低一点）
        @Bean
        @ConditionalOnMissingBean(name = "foo")
        public Bean2 foo() {
            return new Bean2();
        }
    }

    static class Registry implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            registry.registerBeanDefinition("bean2",
                    BeanDefinitionBuilder.genericBeanDefinition(Bean2.class).getBeanDefinition());

            AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(Config0.class);
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
        }
    }

    static class Selector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{Config0.class.getName()};
            // return new String[]{Bean1.class.getName(), Bean2.class.getName()};
        }
    }

    // 1. 同一配置中，@Import 先解析，@Bean 后解析
    // 2. 如果是同名定义，默认后面解析的会覆盖前面解析的
    //
    // DeferredSelector 推迟 @Import，会先解析 @Bean，然后解析 @Import
    static class DeferredSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{SlaveConfig.class.getName()};
        }
    }

    static class Bean1 {}

    static class Bean2 {}

    interface Setting {}

    static class LocalSetting implements Setting {}

    static class DevSetting implements Setting {}

}
