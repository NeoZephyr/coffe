package com.pain.green.service;

import com.pain.green.annotation.Validate;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

    public void hello() {
        System.out.println("hello");
    }

    @Validate(value = true)
    @Override
    public void hello(String word, String name) {
        System.out.printf("%s: %s\n", word, name);
    }

    public void morning() {
        System.out.println("morning");
        throw new RuntimeException("not support morning");
    }

    @Override
    public String how() {
        return "ok";
    }
}
