package com.pain.flame.punk.service;

import com.pain.flame.punk.bean.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PingService {

    @Autowired
    private String serviceName;

    @Autowired
    private List<Student> students;

    public PingService(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<Student> getStudents() {
        return students;
    }
}
