package com.pain.green.resource.conversion.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class GenericApiDemo {
    public static void main(String[] args) {
        Class intClazz = int.class;
        Class arrayClazz = Object[].class;
        Class stringClazz = String.class;

        ParameterizedType parameterizedType = (ParameterizedType) ArrayList.class.getGenericSuperclass();

        System.out.println(parameterizedType);

        Type[] types = parameterizedType.getActualTypeArguments();

        System.out.println(Arrays.asList(types));
        Stream.of(types).map(TypeVariable.class::cast).forEach(System.out::println);

    }
}
