package com.pain.flame.punk.service;

import com.pain.flame.punk.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public final User admin = new User("jack");

    @Lazy
    @Autowired
    private UserService userService;

    public void pay() {
        System.out.println("Pay order");
    }

    public User getAdmin() {
        return admin;
    }

    public void lockStock() throws InterruptedException {
        System.out.println("Lock stock");
        Thread.sleep(1000);
    }
}
