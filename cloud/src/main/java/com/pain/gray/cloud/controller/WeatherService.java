package com.pain.gray.cloud.controller;

public class WeatherService {

    private WeatherSource weatherSource;

    public WeatherService(WeatherSource weatherSource) {
        this.weatherSource = weatherSource;
    }

    public WeatherSource getWeatherSource() {
        return weatherSource;
    }
}
