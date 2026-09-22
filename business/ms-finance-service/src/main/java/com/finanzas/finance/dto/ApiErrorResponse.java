package com.finanzas.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * DTO estandarizado para respuestas de error de la API.
 * 
 * Proporciona formato consistente para todos los errores HTTP,
 * facilitando el debugging y el manejo de errores en cliente.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private boolean success = false;
    private String message;
    private int status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private String path;
    private Object details;

    public ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(String message, int status, String path) {
        this();
        this.message = message;
        this.status = status;
        this.path = path;
    }

    public ApiErrorResponse(String message, int status, String path, Object details) {
        this(message, status, path);
        this.details = details;
    }

    public static ApiErrorResponse of(String message, int status, String path) {
        return new ApiErrorResponse(message, status, path);
    }

    public static ApiErrorResponse of(String message, int status, String path, Object details) {
        return new ApiErrorResponse(message, status, path, details);
    }

    public static ApiErrorResponse of(HttpServletRequest request, String message, int status) {
        return new ApiErrorResponse(message, status, request.getRequestURI());
    }

    public static ApiErrorResponse of(HttpServletRequest request, String message, int status, Object details) {
        return new ApiErrorResponse(message, status, request.getRequestURI(), details);
    }

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }
}
