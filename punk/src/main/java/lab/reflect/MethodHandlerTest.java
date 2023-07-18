package lab.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class MethodHandlerTest {
    public static void main(String[] args) throws Throwable {
        // test1();
        // test2();
        test3();
    }

    private static void test1() throws Throwable {
        MethodHandles.Lookup lookup = Foo.lookup();
        Method method1 = Foo.class.getDeclaredMethod("bar", Object.class);
        MethodHandle methodHandle = lookup.unreflect(method1);
        methodHandle.invoke(null);

        Method method2 = Foo.class.getDeclaredMethod("foo", Object.class);
        methodHandle = lookup.unreflect(method2);
        methodHandle.invoke(new Foo(), null);
    }

    private static void test2() throws Throwable {
        MethodHandles.Lookup lookup = Foo.lookup();
        MethodType methodType = MethodType.methodType(void.class, Object.class);
        MethodHandle methodHandle = lookup.findStatic(Foo.class, "bar", methodType);
        methodHandle.invoke(null);

        methodHandle = lookup.findVirtual(Foo.class, "foo", methodType);
        methodHandle.invoke(new Foo(), null);
        methodHandle.invokeExact(new Foo(), new Object());
    }

    private static void test3() throws Throwable {
        MethodHandles.Lookup lookup = Foo.lookup();
        MethodType methodType = MethodType.methodType(void.class, Object.class);
        MethodHandle methodHandle = lookup.findStatic(Foo.class, "baz", methodType);
        methodHandle.invokeExact(new Object());
    }
}

class Foo {
    public static MethodHandles.Lookup lookup() {
        return MethodHandles.lookup();
    }

    private static void bar(Object o) {
        System.out.println("bar");
    }

    private void foo(Object o) {
        System.out.println("foo");
    }

    private static void baz(Object o) {
        new Exception().printStackTrace();
    }
}