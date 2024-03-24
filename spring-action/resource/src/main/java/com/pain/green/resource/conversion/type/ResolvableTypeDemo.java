package com.pain.green.resource.conversion.type;

import org.springframework.core.ResolvableType;

public class ResolvableTypeDemo {
    public static void main(String[] args) {
        ResolvableType resolvableType = ResolvableType.forClass(MyHashMap.class);

        System.out.println(resolvableType.getSuperType());
        System.out.println(resolvableType.getSuperType().getSuperType());

        System.out.println(resolvableType.asMap());
        System.out.println(resolvableType.asMap().resolve());
        System.out.println(resolvableType.asMap().resolveGeneric(0));
    }
}
