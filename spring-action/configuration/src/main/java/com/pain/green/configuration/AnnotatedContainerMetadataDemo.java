package com.pain.green.configuration;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.util.Map;

@ImportResource("classpath:/dependency-lookup-context.xml")
@PropertySource("classpath:/META-INF/user.properties")
@PropertySource("classpath:/META-INF/user.properties")
@Import(User.class)
public class AnnotatedContainerMetadataDemo {

    @Bean
    public User adminUser(@Value("${user.id}") long id, @Value("${user.name}") String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);

        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotatedContainerMetadataDemo.class);
        applicationContext.refresh();

        Map<String, User> beansMap = applicationContext.getBeansOfType(User.class);

        for (Map.Entry<String, User> beanEntry : beansMap.entrySet()) {
            String beanName = beanEntry.getKey();
            User bean = beanEntry.getValue();

            System.out.printf("beanName: %s, bean: %s\n", beanName, bean);
        }

        applicationContext.close();
    }
}
