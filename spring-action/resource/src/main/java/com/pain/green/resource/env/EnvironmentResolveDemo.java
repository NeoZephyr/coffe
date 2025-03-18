package com.pain.green.resource.env;

import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public class EnvironmentResolveDemo {

    public static void main(String[] args) throws NoSuchFieldException, IOException {
        QualifierAnnotationAutowireCandidateResolver resolver =
                new QualifierAnnotationAutowireCandidateResolver();
        Object a = resolver.getSuggestedValue(
                new DependencyDescriptor(Foo.class.getDeclaredField("a"), false));
        Object b = resolver.getSuggestedValue(
                new DependencyDescriptor(Foo.class.getDeclaredField("b"), false));
        Object c = resolver.getSuggestedValue(
                new DependencyDescriptor(Foo.class.getDeclaredField("c"), false));

        StandardEnvironment environment = new StandardEnvironment();

        // 添加自定义键值
        // environment.getPropertySources().addLast(new ResourcePropertySource("jdbc", new ClassPathResource("jdbc.properties")));

        System.out.println("a = " + a);

        System.out.println("b = " + b + ", resolved b = " + environment.resolvePlaceholders(b.toString()));

        // 解析 EL 表达式
        String v = environment.resolvePlaceholders(c.toString());
        Object vv = new StandardBeanExpressionResolver().evaluate(
                v, new BeanExpressionContext(new DefaultListableBeanFactory(), null));
        System.out.println("c = " + c + ", resolved c = " + vv);
    }

    static class Foo {
        @Value("abc")
        String a;

        @Value("${java.home}")
        String b;

        @Value("#{'class version:' + '${java.class.version}'}")
        String c;
    }
}
