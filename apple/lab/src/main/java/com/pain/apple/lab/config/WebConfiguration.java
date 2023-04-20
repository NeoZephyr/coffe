package com.pain.apple.lab.config;

import com.pain.apple.lab.controller.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;

    /**
     * 参考 CorsFilter
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods(CorsConfiguration.ALL)
                .allowedHeaders(CorsConfiguration.ALL)
                .allowCredentials(true) // 是否发送 cookie 信息
                // .exposedHeaders("X-Authenticate")
                .maxAge(3600); // 1 小时内不需要再预检（发 OPTIONS 请求）
    }

    /**
     * 静态资源的映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");

        // WebMvcConfigurer.super.addInterceptors(registry);
    }
}
