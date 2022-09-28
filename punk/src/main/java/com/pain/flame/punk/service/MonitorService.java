package com.pain.flame.punk.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MonitorService implements InitializingBean {

    @Autowired
    private KafkaService kafkaService;

    @PostConstruct
    public void init() {
        kafkaService.check();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        kafkaService.check();
    }
}
