package com.pain.apple.lab.service;

import com.pain.apple.lab.domain.User;
import com.pain.apple.lab.domain.Worker;
import com.pain.apple.lab.mapper.WorkerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public final User admin = new User("jack");

    @Lazy
    @Autowired
    private UserService userService;

    private final WorkerMapper workerMapper;

    public UserService(WorkerMapper workerMapper) {
        this.workerMapper = workerMapper;
    }


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

    public void doSth(String name) {
        Worker worker = new Worker();
        worker.setName(name);
        workerMapper.save(worker);
    }
}
