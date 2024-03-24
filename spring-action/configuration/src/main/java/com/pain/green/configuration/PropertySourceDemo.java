package com.pain.green.configuration;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@PropertySource("classpath:/META-INF/user.properties")
public class PropertySourceDemo {

    @Bean
    public User user(@Value("${user.id}") long id, @Value("${user.name}") String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);

        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(PropertySourceDemo.class);

        Map propertySourceMap = new HashMap();
        propertySourceMap.put("user.name", "赵破奴");
        org.springframework.core.env.PropertySource customPropertySource = new MapPropertySource("customProperties", propertySourceMap);
        applicationContext.getEnvironment().getPropertySources().addFirst(customPropertySource);
        applicationContext.refresh();

        User user = applicationContext.getBean(User.class);

        System.out.println("user: " + user);
        System.out.println(applicationContext.getEnvironment().getPropertySources());

        applicationContext.close();
    }
}
