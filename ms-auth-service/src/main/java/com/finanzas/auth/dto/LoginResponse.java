package com.finanzas.auth.dto;

public class LoginResponse {
    
    private String message;
    private boolean success;
    private String username;
    private String token;

    public LoginResponse() {}

    public LoginResponse(String message, boolean success, String username) {
        this.message = message;
        this.success = success;
        this.username = username;
    }

    public LoginResponse(String message, boolean success, String username, String token) {
        this.message = message;
        this.success = success;
        this.username = username;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
