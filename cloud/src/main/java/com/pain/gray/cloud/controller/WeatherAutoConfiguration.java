package com.pain.gray.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WeatherSource.class)
@ConditionalOnProperty(name = "weather.enable", havingValue = "true")
public class WeatherAutoConfiguration {

    @Autowired
    private WeatherSource weatherSource;

    @Bean
    @ConditionalOnMissingBean(WeatherService.class)
    public WeatherService weatherService() {
        // org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.pain.gray.cloud.controller.WeatherAutoConfiguration
        return new WeatherService(weatherSource);
    }
}
