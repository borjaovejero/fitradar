package com.fitradar.frontend.wellness.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitradar.frontend.config.BackendConfig;
import com.fitradar.frontend.shared.util.BasicAuthUtils;
import com.fitradar.frontend.wellness.dto.WellnessRequest;
import com.fitradar.frontend.wellness.dto.WellnessResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class WellnessService {

    private static final String BASE_PATH = "/api/wellness";

    private final BackendConfig backendConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public WellnessService(BackendConfig backendConfig, ObjectMapper objectMapper) {
        this.backendConfig = backendConfig;
        this.objectMapper  = objectMapper;
        this.httpClient    = HttpClient.newHttpClient();
    }

    public WellnessResponse createRecord(String username, String password, WellnessRequest body)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + BASE_PATH;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);
        String json       = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), WellnessResponse.class);
        }

        throw new RuntimeException("Error al guardar wellness: " + response.body());
    }

    public WellnessResponse updateRecord(String username, String password, Long id, WellnessRequest body)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + BASE_PATH + "/" + id;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);
        String json       = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), WellnessResponse.class);
        }

        throw new RuntimeException("Error al actualizar wellness: " + response.body());
    }

    public List<WellnessResponse> getRecords(String username, String password)
            throws IOException, InterruptedException {

        // URL corregida: ya no lleva /{username}, el backend lo saca de la sesión
        String url        = backendConfig.getBackendBaseUrl() + BASE_PATH;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), new TypeReference<List<WellnessResponse>>() {});
        }

        throw new RuntimeException("Error al obtener histórico de wellness: " + response.body());
    }
}