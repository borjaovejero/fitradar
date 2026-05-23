package com.fitradar.frontend.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitradar.frontend.config.BackendConfig;
import com.fitradar.frontend.dashboard.dto.DashboardResponse;
import com.fitradar.frontend.shared.util.BasicAuthUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class DashboardService {

    private final BackendConfig backendConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public DashboardService(BackendConfig backendConfig, ObjectMapper objectMapper) {
        this.backendConfig = backendConfig;
        this.objectMapper  = objectMapper;
        this.httpClient    = HttpClient.newHttpClient();
    }

    public DashboardResponse getDashboard(String username, String password)
            throws IOException, InterruptedException {

        // URL corregida: ya no lleva /{username}
        String url        = backendConfig.getBackendBaseUrl() + "/api/dashboard";
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), DashboardResponse.class);
        }
        throw new RuntimeException("Error al obtener dashboard: " + response.body());
    }
}