package com.pain.apple.lab.controller;

import com.pain.apple.lab.codec.MD5Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/")
public class PingController {

    @GetMapping("/ping")
    public String ping() throws NoSuchAlgorithmException {
        return MD5Utils.md5("pong");
    }
}
