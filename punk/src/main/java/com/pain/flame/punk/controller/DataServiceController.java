package com.pain.flame.punk.controller;

import com.pain.flame.punk.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataServiceController {

    @Autowired
    DataService redisDataService;

    @RequestMapping(path = "data/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") int id) {
        redisDataService.delete(id);
    }
}
