package com.pain.green.resource.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ValueAnnotationDemo {

    @Value("${user.name}")
    String userName;

    // QualifierAnnotationAutowireCandidateResolver
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ValueAnnotationDemo.class);
        applicationContext.refresh();
        ValueAnnotationDemo annotationDemo = applicationContext.getBean(ValueAnnotationDemo.class);

        System.out.println(annotationDemo.userName);

        applicationContext.close();
    }
}
