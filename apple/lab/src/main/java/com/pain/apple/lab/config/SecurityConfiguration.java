package com.pain.apple.lab.config;

import com.pain.apple.lab.config.auth.SpringAuthenticationProvider;
import com.pain.apple.lab.config.auth.MfaFilter;
import com.pain.apple.lab.config.auth.SecurityCodeAuthenticationProvider;
import com.pain.apple.lab.config.auth.UserCredentialAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    private final SpringAuthenticationProvider springAuthenticationProvider;

    private final UserCache userCache;

    // 双因子
    private final UserCredentialAuthenticationProvider userCredentialAuthenticationProvider;
    private final SecurityCodeAuthenticationProvider securityCodeAuthenticationProvider;

    private final MfaFilter mfaFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // 双因子
        http.addFilterAt(mfaFilter, BasicAuthenticationFilter.class);

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session 无状态，防止大量 session
                .and()
                // 对所有访问 HTTP 端点的请求进行限制
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/user/*").authenticated() // 需要执行认证
                .mvcMatchers(HttpMethod.GET, "/user/*").permitAll()
                .anyRequest().denyAll();

        // 执行表单登录认证
        http
                .formLogin()
                .loginPage("/login.html") // 自定义登录页面
                .loginProcessingUrl("/login") // 登录表单提交时的处理地址
                .defaultSuccessUrl("/index"); // 登录认证成功后的跳转页面

        // HTTP 基础认证，没有定制的登录页面
        http.httpBasic(Customizer.withDefaults());

        http.authenticationProvider(springAuthenticationProvider);
        // http.authenticationProvider(authenticationProvider());

        // 双因子
        http.authenticationProvider(userCredentialAuthenticationProvider);
        http.authenticationProvider(securityCodeAuthenticationProvider);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());

        // 缓存
        provider.setUserCache(userCache);
        provider.setUserDetailsService(null);
        return provider;
    }
}
