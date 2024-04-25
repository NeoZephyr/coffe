package com.pain.apple.lab.config.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserCredentialAuthentication extends UsernamePasswordAuthenticationToken {

    public UserCredentialAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public UserCredentialAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
