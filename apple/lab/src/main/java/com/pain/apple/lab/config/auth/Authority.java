package com.pain.apple.lab.config.auth;

import lombok.Data;

@Data
public class Authority {
    private Integer id;
    private String name;
    private User user;
}
