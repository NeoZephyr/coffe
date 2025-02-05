package com.pain.green.aspect;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

public class PointcutDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        AspectJExpressionPointcut p1 = new AspectJExpressionPointcut();
        p1.setExpression("execution (* bar())");
        System.out.println("匹配 foo 方法：" + p1.matches(A.class.getMethod("foo"), A.class));
        System.out.println("匹配 bar 方法：" + p1.matches(A.class.getMethod("bar"), A.class));

        AspectJExpressionPointcut p2 = new AspectJExpressionPointcut();
        p2.setExpression("@annotation (org.springframework.transaction.annotation.Transactional)");
        System.out.println("匹配 foo 方法：" + p2.matches(A.class.getMethod("foo"), A.class));
        System.out.println("匹配 bar 方法：" + p2.matches(A.class.getMethod("bar"), A.class));

        StaticMethodMatcherPointcut p3 = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                MergedAnnotations annotations = MergedAnnotations.from(method);

                // 检查方法上是否加注解
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }

                // annotations = MergedAnnotations.from(targetClass); // 只在本类范围查找
                annotations = MergedAnnotations.from(targetClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

                // 检查类上是否加注解
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }

                return false;
            }
        };
        System.out.println("匹配 foo 方法：" + p3.matches(A.class.getMethod("foo"), A.class));
        System.out.println("匹配 bar 方法：" + p3.matches(A.class.getMethod("bar"), A.class));
        System.out.println("匹配 foo 方法：" + p3.matches(B.class.getMethod("foo"), B.class));
        System.out.println("匹配 foo 方法：" + p3.matches(C.class.getMethod("foo"), C.class));
    }

    static class A {
        @Transactional
        public void foo() {}

        public void bar() {}
    }

    @Transactional
    static class B {
        public void foo() {}
    }

    @Transactional
    interface I {}

    static class C implements I {
        public void foo() {}
    }
}
