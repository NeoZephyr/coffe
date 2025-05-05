package com.pain.green.ioc.bean;

import com.pain.green.ioc.domain.City;
import com.pain.green.ioc.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.*;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class BeanDefinitionTests {

    public static void main(String[] args) {
        // readBeanDefinition();
        registerBeanDefinition();
    }

    private static void readBeanDefinition() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        String[] bdNames = beanFactory.getBeanDefinitionNames();
        System.out.println("beanNames: " + Arrays.toString(bdNames));

        // properties 文件中获取
        PropertiesBeanDefinitionReader reader1 = new PropertiesBeanDefinitionReader(beanFactory);
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:/META-INF/user.properties");
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        reader1.loadBeanDefinitions(encodedResource);
        bdNames = beanFactory.getBeanDefinitionNames();
        System.out.println("beanNames: " + Arrays.toString(bdNames));

        // yaml 文件读取
        XmlBeanDefinitionReader reader2 = new XmlBeanDefinitionReader(beanFactory);
        reader2.loadBeanDefinitions("classpath:/yaml-map-context.xml");
        bdNames = beanFactory.getBeanDefinitionNames();
        System.out.println("beanNames: " + Arrays.toString(bdNames));

        // xml 文件中获取
        XmlBeanDefinitionReader reader3 = new XmlBeanDefinitionReader(beanFactory);
        reader3.loadBeanDefinitions(new ClassPathResource("bean-definitions-context.xml"));
        bdNames = beanFactory.getBeanDefinitionNames();
        System.out.println("beanNames: " + Arrays.toString(bdNames));

        // 从配置类中获取
        beanFactory.registerBeanDefinition(
                "config",
                BeanDefinitionBuilder.genericBeanDefinition(Config.class).getBeanDefinition());
        ConfigurationClassPostProcessor postProcessor = new ConfigurationClassPostProcessor();
        postProcessor.postProcessBeanDefinitionRegistry(beanFactory);
        bdNames = beanFactory.getBeanDefinitionNames();
        System.out.println("beanNames:" + Arrays.toString(bdNames));

        // 扫描
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
        scanner.scan("com.pain.green.ioc.bean");
        bdNames = beanFactory.getBeanDefinitionNames();
        System.out.println("beanNames: " + Arrays.toString(bdNames));
    }

    private static void registerBeanDefinition() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(Config.class);

        applicationContext.register(YamlConfig.class);

        // 创建 BeanDefinition 方式一
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("id", 2L)
                .addPropertyValue("name", "里根");
        AbstractBeanDefinition bd1 = beanDefinitionBuilder.getBeanDefinition();

        // 附加属性，不影响 bean populate initialize
        // bd1.setAttribute("city", City.BeiJing.name());
        // bd1.setSource(null);

        // 指定 beanName 注册
        applicationContext.registerBeanDefinition("user2", bd1);

        // 创建 BeanDefinition 方式二
        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(User.class);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("id", 3L)
                .add("name", "布什");
        bd2.setPropertyValues(propertyValues);

        // 不指定 beanName 注册
        BeanDefinitionReaderUtils.registerWithGeneratedName(bd2, applicationContext);

        // 外部单体对象注册
        SingletonBeanRegistry singletonBeanRegistry = applicationContext.getBeanFactory();
        User user = new User();
        user.setId(4L);
        user.setName("克林顿");
        singletonBeanRegistry.registerSingleton("user4", user);

        applicationContext.refresh();

        System.out.println("config bean: " + applicationContext.getBeansOfType(Config.class));

        Map<String, User> beansOfType = applicationContext.getBeansOfType(User.class);

        beansOfType.forEach((k, v) -> {
            System.out.println("k: " + k + ", v: " + v);
        });

        applicationContext.close();
    }


    public static class YamlPropertySourceFactory implements PropertySourceFactory {

        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
            YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
            yamlPropertiesFactoryBean.setResources(resource.getResource());
            Properties yamlProperties = yamlPropertiesFactoryBean.getObject();

            return new PropertiesPropertySource(name, yamlProperties);
        }
    }

    @Component
    public static class Config {

        @Bean(name = {"user1", "alias-user"})
        public User user() {
            User user = new User();
            user.setId(1L);
            user.setName("宇文泰");

            return user;
        }
    }

    @PropertySource(
            name = "yamlPropertySource",
            value = "classpath:/META-INF/user.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class YamlConfig {
        // user.name 多个环境变量的优先级确定？
        @Bean(name = {"user0"})
        public User user(@Value("${user.id}") long id, @Value("${user.name}") String name, @Value("${user.city}") City city) {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setCity(city);
            return user;
        }
    }

    @Component
    public static class Foo {}
}
