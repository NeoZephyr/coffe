package com.pain.green;

import com.pain.green.annotation.AnnotationTests;
import com.pain.green.service.FooService;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Clock;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
        // applicationContext(args);
        // aspectjAop(args);
        // AnnotationTests.componentScanTest();
        // AnnotationTests.importTest();
        AnnotationTests.profileTest();
    }

    /**
     * ApplicationContext 在 BeanFactory 上面的增强功能
     */
    private static void applicationContext(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // BeanFactory 是 ApplicationContext 的父接口
        // ApplicationContext 一部分功能组合了 BeanFactory 的功能
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        // 管理单列对象
        Field field = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        field.setAccessible(true);
        Map<String, Object> map = (Map<String, Object>) field.get(beanFactory);
        map.forEach((k, v) -> System.out.println(k + " = " + v));

        // ApplicationContext 继承接口
        // MessageSource, ResourcePatternResolver, ApplicationEventPublisher, EnvironmentCapable

        // 国际化
        System.out.println(context.getMessage("hello", null, Locale.CHINA));

        // 根据通配符获取资源（类路径、磁盘路径）
        Resource[] resources = context.getResources("classpath*:META-INF/spring.factories");

        for (Resource resource : resources) {
            System.out.println(resource);
        }

        // 环境变量
        System.out.println(context.getEnvironment().getProperty("JAVA_HOME"));;
        System.out.println(context.getEnvironment().getProperty("server.port"));

        // 发布事件，进行解藕
        context.publishEvent(new FooEvent("application"));
    }

    private static void aspectjAop(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        FooService service = context.getBean(FooService.class);

        // 1. 静态切面织入（非代理增强），在编译阶段增强
        // 通过 aspectj 编译器改动 class 类文件，实现功能增强
        // 需要调用 mvn compile 调用 aspectj 插件编译
        // 静态切面织入的方式，可以突破代理方式的限制，比如静态方法也可以增强
        service.foo();

        // 与 Spring 无关，直接创建也能够织入
        new FooService().foo();
        context.close();

        // 2. 此外还有其他的非代理模式的增强，-javaagent:abc.jar
        // 在类加载阶段，进行字节码改写和增强
        // 可以使用 arthas 工具的 jad 命令查看类
    }

    @Component
    static class Bean {

        // @EventListener 注解方法接受事件
        @EventListener
        public void receive(FooEvent event) {
            System.out.println(event);
        }
    }

    static class FooEvent extends ApplicationEvent {
        public FooEvent(Object source) {
            super(source);
        }

        public FooEvent(Object source, Clock clock) {
            super(source, clock);
        }
    }
}
