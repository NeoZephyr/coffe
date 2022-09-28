package com.pain.flame.punk.service;

import com.pain.flame.punk.domain.Worker;
import com.pain.flame.punk.mapper.WorkerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class WorkerService {

    @Autowired
    private WorkerMapper workerMapper;

    @Autowired
    private CourseService courseService;

    @Transactional(rollbackFor = Exception.class)
    public void save(String name) throws Exception {
        Worker worker = new Worker();
        worker.setName(name);
        workerMapper.save(worker);

        try {
            System.out.println(worker.getId());
            courseService.regCourse(worker.getId());
            courseService.regCourse(worker.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (true) {
            // throw new RuntimeException("=====");
        }

        log.info("===== after save worker");
    }
}
