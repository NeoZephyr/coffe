package foundation.lab.reflect;

import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // testOverload();
        testGeneric();
    }

    private static void testOverload() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        val person = new Person();
        person.setAge(10);
        person.setAge(Integer.valueOf(10));

        Method method1 = person.getClass().getDeclaredMethod("setAge", int.class);
        method1.invoke(person, 10);
        method1.invoke(person, Integer.valueOf(10));

        Method method2 = person.getClass().getDeclaredMethod("setAge", Integer.class);
        method2.invoke(person, 10);
        method2.invoke(person, Integer.valueOf(10));
    }

    private static void testGeneric() {
        StringCounter stringCounter = new StringCounter();
        Arrays.stream(stringCounter.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("count"))
                .forEach(method -> {
                    try {
                        method.invoke(stringCounter, "hello");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        System.out.println("===");

        RealStringCounter realStringCounter = new RealStringCounter();

        Arrays.stream(realStringCounter.getClass().getMethods())
                .filter(method -> method.getName().equals("count") && !method.isBridge())
                .findFirst()
                .ifPresent(method -> {
                    try {
                        method.invoke(realStringCounter, "hello");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    static class Person {
        int age;

        public void setAge(int age) {
            System.out.println("int age");
            this.age = age;
        }

        public void setAge(Integer age) {
            System.out.println("Integer age");
            this.age = age;
        }
    }

    static class Counter<T> {
        public void count(T value) {
            System.out.println("counter count: " + value);
        }
    }

    static class StringCounter extends Counter {
        public void count(String value) {
            System.out.println("string counter count: " + value);
        }
    }

    static class RealStringCounter extends Counter<String> {
        @Override
        public void count(String value) {
            System.out.println("real string counter: " + value);
        }
    }
}
