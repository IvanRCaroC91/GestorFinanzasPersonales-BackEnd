package com.finanzas.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    
    private String message;
    private boolean success;
    private String username;
    private String email;
    private String nombreCompleto;
    private String iniciales;
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

    public LoginResponse(String message, boolean success, String username, String email, 
                      String nombreCompleto, String iniciales, String token) {
        this.message = message;
        this.success = success;
        this.username = username;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.iniciales = iniciales;
        this.token = token;
    }

    // Getters y Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getIniciales() {
        return iniciales;
    }

    public void setIniciales(String iniciales) {
        this.iniciales = iniciales;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
