package com.pain.apple.lab.service;

import com.pain.apple.lab.domain.Worker;
import com.pain.apple.lab.mapper.WorkerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
public class WorkerService implements ApplicationContextAware {

    private final WorkerMapper workerMapper;

    private final CourseService courseService;

    private final UserService userService;

    private ApplicationContext context;

    public WorkerService(WorkerMapper workerMapper, CourseService courseService, UserService userService) {
        this.workerMapper = workerMapper;
        this.courseService = courseService;
        this.userService = userService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

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

    public void testSave() throws Exception {
        WorkerService workerService = context.getBean(WorkerService.class);
        workerService.doSave("xxxxx");
    }

    @Transactional
    public void doSave(String name) throws Exception {
        userService.doSth(name);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        log.info("=== save worker");
    }
}
