package com.pain.green.configuration;

import com.pain.green.ioc.domain.City;
import com.pain.green.ioc.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@PropertySource(
        name = "yamlPropertySource",
        value = "classpath:/user.yaml",
        factory = YamlPropertySourceFactory.class)
public class BeanDefinitionDemo {
    public static void main(String[] args) {
        // propertiesBeanDefinitionTest();
        // yamlBeanDefinitionTest();
        annotatedYamlBeanDefinitionTest();
    }

    private static void beanDefinitionTest() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("name", "于景");
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        // 附加属性，不影响 bean populate,initialize
        beanDefinition.setAttribute("name", "侯景");
        // beanDefinition.setSource(BeanDefinitionDemo.class);

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (ObjectUtils.nullSafeEquals("user", beanName) && User.class.equals(bean.getClass())) {
                    BeanDefinition bd = beanFactory.getBeanDefinition(beanName);

                    if (BeanDefinitionDemo.class.equals(bd.getSource())) {
                        String realName = (String) bd.getAttribute("name");
                        User user = (User) bean;
                        user.setName(realName);
                    }
                }

                return bean;
            }
        });

        beanFactory.registerBeanDefinition("user", beanDefinition);

        User user = beanFactory.getBean("user", User.class);

        System.out.println("user: " + user);
    }

    private static void propertiesBeanDefinitionTest() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        PropertiesBeanDefinitionReader beanDefinitionReader = new PropertiesBeanDefinitionReader(beanFactory);
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:/META-INF/user.properties");
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");

        int beanDefinitionCount = beanDefinitionReader.loadBeanDefinitions(encodedResource);

        System.out.println("load beanDefinition count: " + beanDefinitionCount);

        User user = beanFactory.getBean("user", User.class);

        System.out.println("user: " + user);
    }

    private static void yamlBeanDefinitionTest() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("classpath:/yaml-map-context.xml");

        Map yamlMap = beanFactory.getBean("yamlMap", Map.class);

        System.out.println("yamlMap: " + yamlMap);
    }

    private static void annotatedYamlBeanDefinitionTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanDefinitionDemo.class);
        applicationContext.refresh();

        User user = applicationContext.getBean("user", User.class);

        System.out.println("user: " + user);

        applicationContext.close();
    }

    @Bean
    public User user(@Value("${user.id}") long id, @Value("${user.name}") String name, @Value("${user.city}") City city) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setCity(city);

        return user;
    }
}
