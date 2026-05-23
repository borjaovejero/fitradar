package com.fitradar.frontend.risk.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitradar.frontend.config.BackendConfig;
import com.fitradar.frontend.risk.dto.RiskPredictionRequest;
import com.fitradar.frontend.risk.dto.RiskPredictionResponse;
import com.fitradar.frontend.shared.util.BasicAuthUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class RiskPredictionService {

    private static final String BASE_PATH = "/api/risk-predictions";

    private final BackendConfig backendConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public RiskPredictionService(BackendConfig backendConfig, ObjectMapper objectMapper) {
        this.backendConfig = backendConfig;
        this.objectMapper  = objectMapper;
        this.httpClient    = HttpClient.newHttpClient();
    }

    // GET /api/risk-predictions — historial completo del usuario
    public List<RiskPredictionResponse> getPredictions(String username, String password)
            throws IOException, InterruptedException {

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
                    new TypeReference<List<RiskPredictionResponse>>() {});
        }
        throw new RuntimeException("Error al cargar predicciones: " + response.body());
    }

    // GET /api/risk-predictions/latest — última predicción del usuario
    public RiskPredictionResponse getLatest(String username, String password)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + BASE_PATH + "/latest";
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) return null;

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), RiskPredictionResponse.class);
        }
        throw new RuntimeException("Error al cargar última predicción: " + response.body());
    }

    // POST /api/risk-predictions/{id}/feedback — feedback del usuario
    // outcome: 0 = no hubo lesión, 1 = sí hubo lesión/molestia
    public void sendFeedback(String username, String password, Long predictionId, int outcome)
            throws IOException, InterruptedException {

        String url        = backendConfig.getBackendBaseUrl() + BASE_PATH + "/" + predictionId + "/feedback";
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        RiskPredictionRequest body = new RiskPredictionRequest();
        body.setOutcome(outcome);
        String json = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error al enviar feedback: " + response.body());
        }
    }
}