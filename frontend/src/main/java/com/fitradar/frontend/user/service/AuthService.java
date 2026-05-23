package com.fitradar.frontend.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitradar.frontend.config.BackendConfig;
import com.fitradar.frontend.shared.util.BasicAuthUtils;
import com.fitradar.frontend.user.dto.LoginRequest;
import com.fitradar.frontend.user.dto.LoginResponse;
import com.fitradar.frontend.user.dto.RegisterRequest;
import com.fitradar.frontend.user.dto.UpdateUserRequest;
import com.fitradar.frontend.user.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AuthService {

    private final BackendConfig backendConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AuthService(BackendConfig backendConfig, ObjectMapper objectMapper) {
        this.backendConfig = backendConfig;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public LoginResponse login(LoginRequest request) throws IOException, InterruptedException {
        String url = backendConfig.getBackendBaseUrl() + "/api/auth/login";
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), LoginResponse.class);
        }

        throw new RuntimeException("Error en login: " + response.body());
    }

    public UserResponse register(RegisterRequest request) throws IOException, InterruptedException {
        String url = backendConfig.getBackendBaseUrl() + "/api/auth/register";
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), UserResponse.class);
        }

        throw new RuntimeException("Error en registro: " + response.body());
    }

    public UserResponse getUser(String username, String password) throws IOException, InterruptedException {
        String url = backendConfig.getBackendBaseUrl() + "/api/auth/users/" + username;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), UserResponse.class);
        }

        throw new RuntimeException("Error al obtener usuario: " + response.body());
    }

    public UserResponse updateUser(String username, String password, UpdateUserRequest request)
            throws IOException, InterruptedException {

        String url = backendConfig.getBackendBaseUrl() + "/api/auth/users/" + username;
        String authHeader = BasicAuthUtils.buildBasicAuthHeader(username, password);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), UserResponse.class);
        }

        throw new RuntimeException("Error al actualizar usuario: " + response.body());
    }
}
