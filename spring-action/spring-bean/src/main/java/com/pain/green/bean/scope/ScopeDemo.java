package com.pain.green.bean.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

public class ScopeDemo {


    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(new DefaultListableBeanFactory());
        ctx.register(Config.class);

        // autowired 注入属性解析
        // ctx.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        ctx.refresh();

        Shell shell = ctx.getBean(Shell.class);

        System.out.println(shell.foo1.getClass());
        System.out.println(shell.foo1);
        System.out.println(shell.foo1);

        System.out.println(shell.foo2.getClass());
        System.out.println(shell.foo2);
        System.out.println(shell.foo2);

        System.out.println(shell.foo3.getObject());
        System.out.println(shell.foo3.getObject());

        ctx.close();
    }

    static class Config {

        // 单列对象的依赖注入只会发生一次， bean2 始终是第一次注入的对象
        // Scope 失效解决方法：
        // 1. 加 @Lazy 注解，生成代理对象
        // 2. 添加注解属性 @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
        // 3. ObjectFactory 方式
        // 4. 注入 ApplicationContext，通过容器获取

        @Bean
        Shell shell(@Lazy Foo foo) {
            return new Shell(foo);
        }

        @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
        @Bean
        Foo foo() {
            return new Foo();
        }
    }

    static class Shell {
        Foo foo1;

        @Autowired
        Foo foo2;

        @Autowired
        ObjectFactory<Foo> foo3;

        @Autowired
        ApplicationContext context;

        public Shell(Foo foo1) {
            this.foo1 = foo1;
        }
    }

    static class Foo {}
}
