package com.pain.apple.lab.config.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class UserCredentialAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 校验用户名、密码
        return new UsernamePasswordAuthenticationToken("", "");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserCredentialAuthentication.class.isAssignableFrom(authentication);
    }
}
