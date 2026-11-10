package com.pain.green.resource.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentPropertySourceChangeDemo {
    @Value("${user.name}")
    String userName;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(EnvironmentPropertySourceChangeDemo.class);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();

        // 动态插入 PropertySource
        Map<String, Object> source = new HashMap<>();
        source.put("user.name", "萧衍");
        MapPropertySource propertySource = new MapPropertySource("customized", source);
        propertySources.addFirst(propertySource);

        applicationContext.refresh();

        // 启动后更改，没有动态刷新能力，bean 注入的变量不会改变
        source.put("user.name", "侯景");

        EnvironmentPropertySourceChangeDemo demo = applicationContext.getBean(EnvironmentPropertySourceChangeDemo.class);

        System.out.println(demo.userName);

        for (PropertySource ps : propertySources) {
            System.out.printf("name: %s, value: %s\n", ps.getName(), ps.getProperty("user.name"));
        }

        applicationContext.close();
    }
}
