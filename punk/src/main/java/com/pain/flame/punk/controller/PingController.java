package com.pain.flame.punk.controller;

import com.pain.flame.punk.bean.Student;
import com.pain.flame.punk.bean.TimeFilter;
import com.pain.flame.punk.bean.User;
import com.pain.flame.punk.service.UserService;
import com.pain.flame.punk.service.PingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
public class PingController {

    @Value("#{student}")
    private Student student;

    @Value("今天天气真好啊")
    private String title;

    @Value("${ip}")
    private String ip;

    @Value("#{student.name}")
    private String name;

    @Autowired
    private PingService pingService;

    @Autowired
    UserService userService;

    // @Autowired
    @Qualifier("com.pain.flame.punk.bean.TimeFilter")
    FilterRegistrationBean timeFilter;

    @GetMapping("/ping")
    public String ping() throws InterruptedException {
        userService.pay();
        log.info("student: {}, title: {}, ip: {}, name: {}, students: {}", student, title, ip, name, pingService.getStudents());
        return "pong";
    }

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @GetMapping("/hello/**")
    public String hello(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String name = antPathMatcher.extractPathWithinPattern(pattern, path);

        log.info("path: {}, pattern: {}, name: {}", path, pattern, name);

        return name;
    }

    @GetMapping("/hi")
    public String hi(@RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        log.info("date: {}", date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @GetMapping(value = "/header", produces = {"application/json"})
    public String header(@RequestHeader() HttpHeaders map, HttpServletResponse response) {
        log.info("map: {}", map);

        response.addHeader("k", "v");
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        return "ok";
    }

    @PostMapping(value = "/user")
    public User user(@Validated @RequestBody User user) {
        log.info("user: {}", user);
        return user;
    }

    @GetMapping(value = "/admin")
    public String admin() {
        return "admin";
    }

    @PostMapping(path = "/form")
    public String form(@RequestParam("p1") String p1, @RequestParam("p2") String p2) {
        return "hello, p1: " + p1 + ", p2: " + p2;
    }

    @GetMapping(path = "/param")
    public String param(@RequestParam("p") String param) {
        return "hello: " + param;
    }
}
