package com.pain.apple.lab.service;

import com.pain.apple.lab.mapper.CourseMapper;
import com.pain.apple.lab.mapper.WorkerCourseMapper;
import com.pain.apple.lab.support.TransactionSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private WorkerCourseMapper workerCourseMapper;

    @Autowired
    private TransactionSupport transactionSupport;

    @Transactional(rollbackFor = Exception.class)
    public void regCourse(int studentId) throws Exception {
        transactionSupport.doAfterCommit(() -> {
            log.info("===== transaction support doAfterCommit");
        });

        workerCourseMapper.save(studentId, 1);
        courseMapper.addCourseNumber(1);
        // throw new Exception("注册失败");
        log.info("===== after regCourse");
    }
}
