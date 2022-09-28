package com.pain.flame.punk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaService {

    public void check() {
        log.info("check kafka service");
    }
}
