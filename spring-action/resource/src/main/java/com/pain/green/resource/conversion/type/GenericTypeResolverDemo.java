package com.pain.green.resource.conversion.type;

import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericTypeResolverDemo {
    public static void main(String[] args) throws NoSuchMethodException {
        // normalType();
        collectionType();

        Map<TypeVariable, Type> typeMap = GenericTypeResolver.getTypeVariableMap(HashMap.class);
        typeMap.forEach((k, v) -> {
            System.out.printf("k: %s, v: %s\n", k, v);
        });

        typeMap = GenericTypeResolver.getTypeVariableMap(MyHashMap.class);

        typeMap.forEach((k, v) -> {
            System.out.printf("class: %s, k: %s, v: %s\n", k.getGenericDeclaration(), k, v);
        });
    }

    private static void normalType() throws NoSuchMethodException {
        Method method = GenericTypeResolverDemo.class.getMethod("getString");

        Class<?> type = GenericTypeResolver.resolveReturnType(method, GenericTypeResolverDemo.class);
        Class<?> typeArg = GenericTypeResolver.resolveReturnTypeArgument(method, Comparable.class);

        System.out.println(type);
        System.out.println(typeArg);
    }

    private static void collectionType() throws NoSuchMethodException {
        Method method = GenericTypeResolverDemo.class.getMethod("getList");

        Class<?> type = GenericTypeResolver.resolveReturnType(method, GenericTypeResolverDemo.class);
        Class<?> typeArg = GenericTypeResolver.resolveReturnTypeArgument(method, List.class);

        System.out.println(type);
        System.out.println(typeArg);
    }

    public static String getString() {
        return "";
    }

    public static List<String> getList() {
        return null;
    }
}
