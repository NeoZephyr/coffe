package com.pain.apple.lab.config.auth;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private Integer id;
    private String username;
    private String password;
    private List<Authority> authorities;
}
