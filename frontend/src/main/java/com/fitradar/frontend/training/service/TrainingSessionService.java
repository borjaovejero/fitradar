package com.fitradar.frontend.training.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitradar.frontend.config.BackendConfig;
import com.fitradar.frontend.shared.util.BasicAuthUtils;
import com.fitradar.frontend.training.dto.TrainingSessionRequest;
import com.fitradar.frontend.training.dto.TrainingSessionResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class TrainingSessionService {

    private final BackendConfig backendConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public TrainingSessionService(BackendConfig backendConfig, ObjectMapper objectMapper) {
        this.backendConfig = backendConfig;
        this.objectMapper  = objectMapper;
        this.httpClient    = HttpClient.newHttpClient();
    }

    public TrainingSessionResponse createSession(String username, String password,
                                                 TrainingSessionRequest body)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + "/api/training-sessions";
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
            return objectMapper.readValue(response.body(), TrainingSessionResponse.class);
        }
        throw new RuntimeException(extractMessage("Error al guardar entrenamiento", response));
    }

    public TrainingSessionResponse updateSession(String username, String password,
                                                 Long id, TrainingSessionRequest body)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + "/api/training-sessions/" + id;
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
            return objectMapper.readValue(response.body(), TrainingSessionResponse.class);
        }
        throw new RuntimeException(extractMessage("Error al actualizar entrenamiento", response));
    }

    public List<TrainingSessionResponse> getSessions(String username, String password)
            throws IOException, InterruptedException {

        // URL corregida: ya no lleva /{username}
        String url        = backendConfig.getBackendBaseUrl() + "/api/training-sessions";
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(),
                    new TypeReference<List<TrainingSessionResponse>>() {});
        }
        throw new RuntimeException(extractMessage("Error al cargar entrenamientos", response));
    }

    public void deleteSession(String username, String password, Long id)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + "/api/training-sessions/" + id;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) return;
        throw new RuntimeException(extractMessage("Error al borrar entrenamiento", response));
    }

    private String extractMessage(String fallback, HttpResponse<String> response) {
        String prefix = fallback + " (HTTP " + response.statusCode() + ")";
        String body   = response.body();
        if (body == null || body.isBlank()) return prefix;
        try {
            JsonNode root    = objectMapper.readTree(body);
            String message   = root.path("message").asText("");
            JsonNode fields  = root.path("fields");
            if (fields.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> it = fields.fields();
                if (it.hasNext()) {
                    String fm = it.next().getValue().asText();
                    return prefix + ": " + (message.isBlank() ? fm : message + " - " + fm);
                }
            }
            if (!message.isBlank()) return prefix + ": " + message;
        } catch (Exception ignored) {}
        return prefix + ": " + body;
    }
}