package com.pain.green.service;

import org.springframework.stereotype.Service;

@Service
public class FooService {
    public void foo() {
        System.out.println(">>> foo 方法调用");
    }
}
