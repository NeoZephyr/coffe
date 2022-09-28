package com.pain.flame.punk.mapper;

import com.pain.flame.punk.domain.Worker;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface WorkerMapper {

    @Insert("INSERT INTO `worker`(`name`) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Worker worker);

}
