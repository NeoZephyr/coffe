package com.pain.apple.lab.config.auth;

import lombok.Data;

@Data
public class UserCredential {
    private Integer id;
    private String username;
    private String password;
}
