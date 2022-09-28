package com.pain.flame.punk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class RedisDataService implements DataService {
    @Override
    public void delete(int id) {
        log.info("redis delete");
    }
}
