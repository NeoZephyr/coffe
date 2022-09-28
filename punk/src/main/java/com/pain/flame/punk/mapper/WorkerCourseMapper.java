package com.pain.flame.punk.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkerCourseMapper {

    @Insert("INSERT INTO `worker_course`(`student_id`, `course_id`) VALUES (#{studentId}, #{courseId})")
    void save(@Param("studentId") Integer workerId, @Param("courseId") Integer courseId);
}
