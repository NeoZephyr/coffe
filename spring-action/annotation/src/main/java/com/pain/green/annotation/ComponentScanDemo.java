package com.pain.green.annotation;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@MyComponentScan(scanBasePackages = "com.pain.green.annotation")
public class ComponentScanDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ComponentScanDemo.class);
        applicationContext.refresh();

        MyBean myBean = applicationContext.getBean(MyBean.class);

        System.out.println(myBean);

        applicationContext.close();
    }
}
