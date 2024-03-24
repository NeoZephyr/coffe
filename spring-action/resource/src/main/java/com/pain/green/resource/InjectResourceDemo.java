package com.pain.green.resource;

import com.pain.green.resource.util.ResourceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

public class InjectResourceDemo {

    @Value("classpath:/META-INF/default.properties")
    Resource defaultPropertiesResource;

    @Value("classpath*:/META-INF/*.properties")
    Resource[] propertiesResources;

    @Value("${user.dir}")
    String currentDir;

    @PostConstruct
    public void init() {
        System.out.println(ResourceUtils.getContent(defaultPropertiesResource));
        System.out.println(currentDir);
        Stream.of(propertiesResources).map(ResourceUtils::getContent).forEach(System.out::println);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(InjectResourceDemo.class);
        applicationContext.refresh();
        applicationContext.close();
    }
}
