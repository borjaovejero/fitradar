package com.fitradar.frontend.user.dto;

public class LoginResponse {

    private String message;
    private String username;

    public LoginResponse() {
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