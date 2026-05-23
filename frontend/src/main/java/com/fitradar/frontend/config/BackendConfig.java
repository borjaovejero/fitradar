package com.fitradar.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackendConfig {

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    public String getBackendBaseUrl() {
        return backendBaseUrl;
    }
}