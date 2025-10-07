package com.pain.gray.cloud.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public class WeatherSource {

    private String rate;

    public String getRate() {
        return rate;
    }
}
