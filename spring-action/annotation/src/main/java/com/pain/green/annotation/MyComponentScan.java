package com.pain.green.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ComponentScan
public @interface MyComponentScan {

    // 子注解提供新的属性方法引用父（元）注解中的属性方法
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    // 覆盖了元注解 basePackages
    @AliasFor("basePackages")
    String[] packages() default {};

    // 与元注解 @ComponentScan 同名属性
    String[] basePackages() default {};
}
