package com.pain.flame.punk.service;

import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Service
public class TestService {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // UserService userService = new UserService();
        UserService userService = UserService.class.newInstance();
        System.out.println(userService.admin);
        System.out.println("=== test");
    }
}
