package com.pain.apple.lab.config.auth;

import lombok.Data;

@Data
public class SecurityCode {
    private Integer id;
    private String username;
    private String code;
}
