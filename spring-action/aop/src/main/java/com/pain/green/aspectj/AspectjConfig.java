package com.pain.green.aspectj;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"com.pain.green.*"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AspectjConfig {
    public AspectjConfig() {
        System.out.println("=== Init AspectjConfig");
    }
}
