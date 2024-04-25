package com.pain.apple.lab.config.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class SecurityCodeAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 安全码验证
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SecurityCodeAuthentication.class.isAssignableFrom(authentication);
    }
}
