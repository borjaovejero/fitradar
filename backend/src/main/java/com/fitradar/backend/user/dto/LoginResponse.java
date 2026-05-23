package com.fitradar.backend.user.dto;

public class LoginResponse {

    private String message;
    private String username;

    public LoginResponse() {
    }

    public LoginResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}