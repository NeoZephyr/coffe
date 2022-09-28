package com.pain.flame.punk;

import java.util.Random;

public class Snap {

    public static final int a = 10;

    static {
        int i = new Random().nextInt(100);

        System.out.println(i);

        if (i > 0) {
            throw new ExceptionInInitializerError("fuck you");
            // throw new RuntimeException("fuck you");
        }
    }

    static void doSomth() {
    }
}
