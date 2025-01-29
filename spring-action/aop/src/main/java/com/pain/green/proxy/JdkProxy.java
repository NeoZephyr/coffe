package com.pain.green.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public class JdkProxy {

    interface Foo {
        void foo();
    }

    static class Target implements Foo {

        @Override
        public void foo() {
            System.out.println(">>> 目标类 foo 方法调用");
        }
    }

    public static void main(String[] args) {
        // jdkProxy();
        mockProxy();
    }

    private static void jdkProxy() {
        // 加载运行期间动态生成的字节码
        ClassLoader classLoader = JdkProxy.class.getClassLoader();
        Target target = new Target();

        // 第二个参数表示代理类要实现的接口
        Foo proxy = (Foo) Proxy.newProxyInstance(classLoader, new Class[]{Foo.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
                System.out.println(">>> before...");
                Object result = method.invoke(target, args); // 代理返回目标方法执行结果
                System.out.println(">>> after...");
                return result;
            }
        });

        // 代理 proxy 与 target 的类都实现了 Foo 接口，彼此之间在类继承树中间是兄弟关系

        proxy.foo();

        // 代理类原理
        // 代理类实现被代理相关接口，比如 Foo 接口
        // 代理类中持有 InvocationHandler 对象
        // 调用接口方法时，获取其 Method 对象，然后调用 InvocationHandler 对象的 invoke 方法，传入 Method 对象

        // 代理类生成原理
        // 1. asm 技术生成代理类字节码文件的 byte 数组
        // 2. 自定义的 ClassLoader 通过 super.defineClass 方法将 byte 数组加载为 Class 对象
        // 3. 用反射生成代理类对象

        // 可以用 arthas 工具查看代理类字节码

        // 方法反射调用，jdk 的优化：
        // 刚开始的时候都是 java 本地 api 实现的
        // 调用次数达到一定次数的时候，会生成新实现类，在内部直接调用目标方法，而不是使用反射
    }

    private static void mockProxy() {
        Target target = new Target();
        Foo foo = new Proxy0(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(">>> before...");
                Object result = method.invoke(target, args); // 代理返回目标方法执行结果
                System.out.println(">>> after...");
                return result;
            }
        });
        foo.foo();
    }

    static class Proxy0 extends Proxy implements Foo {

        static Method foo;

        static {
            try {
                foo = Foo.class.getMethod("foo");
            } catch (NoSuchMethodException e) {
                throw new NoSuchMethodError(e.getMessage());
            }
        }

        public Proxy0(InvocationHandler h) {
            super(h);
        }

        @Override
        public void foo() {
            try {
                h.invoke(this, foo, new Object[0]);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    }
}
