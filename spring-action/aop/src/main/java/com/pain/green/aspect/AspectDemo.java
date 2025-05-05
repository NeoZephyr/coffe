package com.pain.green.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AspectDemo {

    public static void main(String[] args) throws Throwable {
        // testAspect();
        // testProxyCreator();
        testAspectConvert();
    }

    private static void testAdvisor() {
        // 准备切入点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* foo())");

        // 准备增强
        MethodInterceptor advice = new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println(">>> before");
                Object result = invocation.proceed();
                System.out.println(">>> after");
                return result;
            }
        };

        // 准备代理
        // 1. proxyTargetClass 为 false 的时候，检查目标类是否实现接口，如果实现接口，使用 jdk 生成代理
        // 2. proxyTargetClass 为 false 的时候，如果目标类没有实现接口，使用 CGLIB 生成代理
        // 3. proxyTargetClass 为 true 的时候，使用 CGLIB 生成代理

        // ProxyFactory 底层用 AopProxyFactory 选择具体代理实现
        // 1. JdkDynamicAopProxy
        // 2. ObjenesisCglibAopProxy
        ProxyFactory factory = new ProxyFactory();
        Target target = new Target();
        factory.setTarget(target);

        // 这种方式，没有指定 pointcut，调用任何方法都会增强
//        factory.addAdvice((MethodInterceptor) invocation -> {
//            System.out.println(">>> before");
//            Object result = invocation.proceed();
//            System.out.println(">>> after");
//            return result;
//        });

        // 准备切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        // 可以加多个切面
        factory.addAdvisors(advisor);

        // 如果要使用 JDK 的动态代理，那么在创建代理之前，加一个接口
        // 因为 JDK 的动态代理是基于接口实现的
        // factory.addInterface(Foo.class);
        // 告知目标类上实现的接口
        // factory.setInterfaces(target.getClass().getInterfaces());

        // 指定了接口，也希望用 CGLib 生成代理
        // factory.setProxyTargetClass(true);

        // 创建代理对象
        // 代理对象中，会间接引用到切面对象
        Foo proxy = (Foo) factory.getProxy();

        System.out.println(proxy.getClass());
        proxy.foo();
    }

    private static void testAspect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("aspect0", Aspect0.class);
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);

        // AnnotationAwareAspectJAutoProxyCreator 是 Bean 后处理器
        // AnnotationAwareAspectJAutoProxyCreator 能识别高级切面中的注解
        // 根据切点表达式查看，有没有匹配的目标，或者这么说，根据 bean 类型查找切面类
        // 有的话，就为目标自动创建代理
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);

        // 创建自动代理的时机
        // 1. 创建 Bean 实例阶段，一般用的少。postProcessBeforeInstantiation
        // 2. 初始化阶段，用的最多。postProcessAfterInitialization
        // 3. 依赖注入阶段，处理循环依赖。通过工厂对象会调用到 getEarlyBeanReference

        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        Method method1 = AbstractAdvisorAutoProxyCreator.class.getDeclaredMethod("findEligibleAdvisors", Class.class, String.class);
        method1.setAccessible(true);

        // 去容器中找，A 类是否有匹配的切面
        List<Advisor> advisors = (List<Advisor>) method1.invoke(creator, A.class, "");

        for (Advisor advisor : advisors) {
            System.out.println(advisor);
        }

        Method method2 = AbstractAutoProxyCreator.class.getDeclaredMethod("wrapIfNecessary", Object.class, String.class, Object.class);
        method2.setAccessible(true);

        // 根据是否找到匹配的切面，决定是否对目标进行增强
        A a = (A) method2.invoke(creator, new A(), "a", "a"); // 创建代理了
        B b = (B) method2.invoke(creator, new B(), "b", "b"); // 没有创建代理

        System.out.println(a.getClass());
        System.out.println(b.getClass());

        a.foo();
        b.bar();
    }

    private static void testProxyCreator() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(Config0.class);

        // 创建代理时机
        // 创建 -> （*）依赖注入 -> 初始化（*）

        // 1. 初始化之后创建代理对象的情况：Bean1 增强，Bean2 不增强，Bean2 依赖 Bean1
        // 1.1 从日志打印中可以看出来，Bean1 的代理对象在 Bean1 的初始化后创建
        // 1.2 然后给 Bean2 注入的是 Bean1 的代理对象

        // 2. 依赖注入之前创建代理的情况：Bean1 增强，Bean2 不增强，Bean2 依赖 Bean1，Bean1 也依赖 Bean2
        // 2.1 Bean1 依赖 Bean2 对象，需要去创建 Bean2
        // 2.2 Bean2 也依赖 Bean1 对象，此时需要注入的最终的代理对象，所以创建 Bean1 的代理对象，再进行注入
        // 2.3 给 Bean2 注入 Bean1 之后，初始化 Bean2
        // 2.4 给 Bean1 注入 Bean2，然后初始化 Bean1
        //
        // 虽然 Bean1 代理对象在依赖注入之前就已经创建了，但是在调用依赖注入、初始化方法的时候，还是在之前没有增强的对象上调用的
        // 也就是就是说依赖注入、初始化不应该增强，仍应该被施加于原始的未增强的对象上面

        context.refresh();
        context.close();
    }

    private static void testAspectConvert() throws Throwable {
        AspectInstanceFactory factory = new SingletonAspectInstanceFactory(new Aspect0());
        List<Advisor> advisors = new ArrayList<>();

        // 高级切面转换为低级切面
        for (Method method : Aspect0.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, factory);
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisors.add(advisor);
            }

            // 除了 Before，类似的增强还有
            // AspectJAroundAdvice
            // AspectJAfterAdvice
            // AspectJAfterReturningAdvice
            // AspectJAfterThrowingAdvice

            // 所有的增强，最终都会统一转换为环绕增强 MethodInterceptor
            // 已经实现 MethodInterceptor 接口的，就不用转换了
        }

        for (Advisor advisor : advisors) {
            System.out.println(advisor);
        }

        System.out.println("<<<<<<");

        ProxyFactory proxyFactory = new ProxyFactory();
        Target target = new Target();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvice(ExposeInvocationInterceptor.INSTANCE); // 作用是：把 invocation 放入当前线程，在最外层
        proxyFactory.addAdvisors(advisors);
        List<Object> interceptors = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo"), Target.class);

        // org.springframework.aop.aspectj.AspectJMethodBeforeAdvice 转换成了
        // org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor

        // 适配器模式
        // 以 @Before 为列，MethodBeforeAdviceAdapter 将 AspectJMethodBeforeAdvice 适配为 MethodBeforeAdviceInterceptor
        for (Object interceptor : interceptors) {
            System.out.println(interceptor);
        }

        // 调用链对象由环绕增强、目标对象两个部分组成
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(
                null, target, Target.class.getMethod("foo"), new Object[0], Target.class, interceptors) {
        };
//        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(
//                null, target, Target.class.getMethod("foo"), new Object[0], Target.class, interceptors);
        methodInvocation.proceed();
    }

    interface Foo {
        void foo();
    }

    static class Target implements Foo {
        @Override
        public void foo() {
            System.out.println(">>> target 类型 foo 方法调用");
        }
    }

    static class A {
        public void foo() {
            System.out.println(">>> A 类 foo 方法调用");
        }
    }

    static class B {
        public void bar() {
            System.out.println(">>> B 类 bar 方法调用");
        }
    }

    // 高级切面
    // 高级切面最终会被转换为低级切面
    @Aspect
    @Order(1) // 切面顺序设置
    static class Aspect0 {
        @Before("execution(* foo())") // -> 转换为一个 advisor 切面
        public void before() {
            System.out.println(">>> before");
        }

        // 动态增强，带参数绑定
        // 动态增强调用：InterceptorAndDynamicMethodMatcher，这个是环绕增强跟切点的组合
        @Before("execution(* foo(..)) && args(x)")
        public void beforeDynamic(int x) {
            System.out.println(">>> before, x: " + x);
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println(">>> after");
        }
    }

    @Configuration
    static class Config {
        // Advisor 低级切面，相比 Aspect 粒度更细
        @Bean
        public Advisor advisor(MethodInterceptor advice) {
            AspectJExpressionPointcut p = new AspectJExpressionPointcut();
            p.setExpression("execution(* foo())");
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(p, advice);

            // 切面顺序设置
            // advisor.setOrder(2);
            return advisor;
        }

        @Bean
        public MethodInterceptor advice() {
            return new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    System.out.println(">>> before");
                    Object result = invocation.proceed();
                    System.out.println(">>> after");
                    return result;
                }
            };
        }
    }

    @Configuration
    static class Config0 {
        // 处理 @Aspect 生成代理
        @Bean
        public AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }

        // 处理 @Autowired
        @Bean
        public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
            return new AutowiredAnnotationBeanPostProcessor();
        }

        // 处理 @PostConstruct
        @Bean
        public CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor() {
            return new CommonAnnotationBeanPostProcessor();
        }

        @Bean
        public Advisor advisor(MethodInterceptor advice) {
            AspectJExpressionPointcut p = new AspectJExpressionPointcut();
            p.setExpression("execution(* foo())");
            return new DefaultPointcutAdvisor(p, advice);
        }

        @Bean
        public MethodInterceptor advice() {
            return new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    System.out.println(">>> before");
                    Object result = invocation.proceed();
                    System.out.println(">>> after");
                    return result;
                }
            };
        }

        // Bean1 会被代理增强
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean1 {
        public Bean1() {
            System.out.println(">>> Bean1 构造方法调用");
        }

        public void foo() {}

        @Autowired
        public void setBean2(Bean2 bean2) {
            System.out.println(">>> Bean2 对象依赖注入到 Bean1 对象，bean2: " + bean2.getClass() + ", bean1: " + this.getClass());
        }

        @PostConstruct
        public void init() {
            System.out.println(">>> Bean1 初始化方法调用，this: " + this.getClass());
        }
    }

    static class Bean2 {
        public Bean2() {
            System.out.println(">>> Bean2 构造方法调用");
        }

        @Autowired
        public void setBean1(Bean1 bean1) {
            System.out.println(">>> Bean1 对象依赖注入到 Bean2 对象，bean1: " + bean1.getClass());
        }

        @PostConstruct
        public void init() {
            System.out.println(">>> Bean2 初始化方法调用");
        }
    }
}
