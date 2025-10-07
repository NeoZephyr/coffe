package com.pain.gray.cloud.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class FooService {

    public FooService() {
        System.out.println("===== init FooService");
    }

    public void foo() {
        System.out.println("===== foo()");
    }
}
