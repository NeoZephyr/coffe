package com.pain.flame.punk.service;

import com.pain.flame.punk.mapper.CourseMapper;
import com.pain.flame.punk.mapper.WorkerCourseMapper;
import com.pain.flame.punk.support.TransactionSupport;
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
