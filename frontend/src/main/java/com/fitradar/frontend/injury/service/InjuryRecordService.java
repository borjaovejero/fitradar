package com.fitradar.frontend.injury.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitradar.frontend.config.BackendConfig;
import com.fitradar.frontend.injury.dto.InjuryRecordRequest;
import com.fitradar.frontend.injury.dto.InjuryRecordResponse;
import com.fitradar.frontend.shared.util.BasicAuthUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class InjuryRecordService {

    private static final String BASE_PATH = "/api/injury-records";

    private final BackendConfig backendConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public InjuryRecordService(BackendConfig backendConfig, ObjectMapper objectMapper) {
        this.backendConfig = backendConfig;
        this.objectMapper  = objectMapper;
        this.httpClient    = HttpClient.newHttpClient();
    }

    public InjuryRecordResponse createRecord(String username, String password,
                                             InjuryRecordRequest body)
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
            return objectMapper.readValue(response.body(), InjuryRecordResponse.class);
        }
        throw new RuntimeException("Error al guardar lesión: " + response.body());
    }

    public InjuryRecordResponse updateRecord(String username, String password,
                                             Long id, InjuryRecordRequest body)
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
            return objectMapper.readValue(response.body(), InjuryRecordResponse.class);
        }
        throw new RuntimeException("Error al actualizar lesión: " + response.body());
    }

    public List<InjuryRecordResponse> getRecords(String username, String password)
            throws IOException, InterruptedException {

        // URL corregida: ya no lleva /{username}
        String url        = backendConfig.getBackendBaseUrl() + BASE_PATH;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(),
                    new TypeReference<List<InjuryRecordResponse>>() {});
        }
        throw new RuntimeException("Error al cargar lesiones: " + response.body());
    }

    public void deleteRecord(String username, String password, Long id)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + BASE_PATH + "/" + id;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) return;
        throw new RuntimeException("Error al borrar lesión: " + response.body());
    }
}