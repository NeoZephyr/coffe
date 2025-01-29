package com.pain.green.ioc.container;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

public class BeanFactoryPostProcessorDemo {

    public static void main(String[] args) throws IOException {
        // mockComponentScan();
        mockBean();
    }

    private static void configurationClassPostProcessor() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean("config", Config.class);
        ctx.registerBean(ConfigurationClassPostProcessor.class); // @Configuration @ComponentScan @Bean @Import @ImportSource

        // 常用的 BeanFactoryPostProcessor 还有 MyBatis 的 MapperScannerConfigurer

        ctx.refresh();

        for (String name : ctx.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        ctx.close();
    }

    private static void mockComponentScan() throws IOException {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean("config", Config.class);

        // 这部分属于自定义扫描 @Component，可以自定义 BeanFactoryPostProcessor/BeanDefinitionRegistryPostProcessor 实现这部分逻辑，然后用 ctx 进行注册
        ComponentScan scan = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);

        if (scan != null) {
            for (String basePackage : scan.basePackages()) {
                String path = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";
                System.out.println("base package path: " + path);

                // new PathMatchingResourcePatternResolver().getResources(path);
                Resource[] resources = ctx.getResources(path);
                CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

                for (Resource resource : resources) {
                    // 不走类加载，效率比较高
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    System.out.println("class 名称: " + metadataReader.getClassMetadata().getClassName());
                    System.out.println("是否加了 @Component 注解: " + metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName()));
                    System.out.println("是否加了 @Component 派生注解: " + metadataReader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName()));
                    System.out.println();

                    if (metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName())
                            || metadataReader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName())) {
                        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                                .genericBeanDefinition(metadataReader.getClassMetadata().getClassName())
                                .getBeanDefinition();
                        AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
                        String beanName = generator.generateBeanName(beanDefinition, ctx.getDefaultListableBeanFactory());
                        ctx.getDefaultListableBeanFactory().registerBeanDefinition(beanName, beanDefinition);
                    }
                }
            }
        }

        ctx.refresh();

        for (String name : ctx.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        ctx.close();
    }

    private static void mockBean() throws IOException {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean("config", Config.class);

        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(
                new ClassPathResource("com/pain/green/ioc/container/BeanFactoryPostProcessorDemo$Config.class"));
        Set<MethodMetadata> methods = metadataReader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());

        for (MethodMetadata method : methods) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();

            // 解析 @Bean 里面的属性
            // 获取 @Bean 里面的注解
            String initMethod = method.getAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();

            // 定义工厂方法 -> method.getMethodName()
            // 工厂对象 -> config
            builder.setFactoryMethodOnBean(method.getMethodName(), "config");

            // 设置装配方式
            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

            if (!initMethod.isEmpty()) {
                builder.setInitMethodName(initMethod);
            }

            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

            // 方法名作为 Bean 名称
            ctx.getDefaultListableBeanFactory().registerBeanDefinition(method.getMethodName(), beanDefinition);
        }

        ctx.refresh();

        for (String name : ctx.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        ctx.close();
    }

    @ComponentScan("com.pain.green.ioc.domain")
    @Configuration
    static class Config {

        @Bean
        public A a(B b) {
            A a = new A();
            a.b = b;
            return a;
        }

        @Bean
        public B b() {
            return new B();
        }
    }

    static class A {

        public B b;

        public A() {
            System.out.println("construct bean A");
        }
    }

    static class B {
        public B() {
            System.out.println("construct bean B");
        }
    }

    /**
     * 模拟 MyBatis 扫描 Mapper
     */
    static class MapperPostProcessor implements BeanDefinitionRegistryPostProcessor {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources("classpath:com/pain/green/ioc/domain/**/*.class");
                AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
                CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

                for (Resource resource : resources) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();

                    if (!classMetadata.isInterface()) {
                        continue;
                    }

                    AbstractBeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class)
                            .addConstructorArgValue(classMetadata.getClassName())
                            .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                            .getBeanDefinition();

                    // 只是为了得到 Mapper Bean 的名称
                    AbstractBeanDefinition nameBd = BeanDefinitionBuilder.genericBeanDefinition(classMetadata.getClassName()).getBeanDefinition();

                    // 如果用 MapperFactorBean 的 BeanDefinition 生成 beanName，那么所有的 Mapper Bean 名称都重复了
                    // String beanName1 = generator.generateBeanName(bd, registry);
                    String beanName = generator.generateBeanName(nameBd, registry);
                    registry.registerBeanDefinition(beanName, bd);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}
    }

    // 模拟 MyBatis 里面的 MapperFactoryBean
    static class MapperFactoryBean{}
}
