package com.pain.apple.lab.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class HttpClientProvider {

    protected RestTemplate createRestTemplate() {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .useSystemProperties()
                .disableRedirectHandling()
                .disableCookieManagement()
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60000);
        return new RestTemplate(requestFactory);
    }
}
