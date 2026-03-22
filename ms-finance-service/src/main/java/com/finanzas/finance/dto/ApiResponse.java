package com.finanzas.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO estandarizado para todas las respuestas de la API.
 * 
 * Proporciona una estructura consistente para todas las respuestas,
 * facilitando el manejo por parte del frontend y estandarizando
 * el formato de salida del microservicio.
 * 
 * @param <T> Tipo de dato contenido en el campo data
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Indica si la operación fue exitosa.
     */
    private boolean success;

    /**
     * Mensaje descriptivo del resultado de la operación.
     */
    private String message;

    /**
     * Datos retornados por la operación (puede ser null).
     */
    private T data;

    /**
     * Timestamp de cuando se generó la respuesta.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Crea una respuesta exitosa con datos.
     * 
     * @param message Mensaje de éxito
     * @param data Datos a retornar
     * @return ApiResponse con éxito
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
            true, 
            message, 
            data, 
            LocalDateTime.now()
        );
    }

    /**
     * Crea una respuesta exitosa sin datos.
     * 
     * @param message Mensaje de éxito
     * @return ApiResponse con éxito y sin datos
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(
            true, 
            message, 
            null, 
            LocalDateTime.now()
        );
    }

    /**
     * Crea una respuesta de error.
     * 
     * @param message Mensaje de error
     * @return ApiResponse con error
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(
            false, 
            message, 
            null, 
            LocalDateTime.now()
        );
    }

    /**
     * Crea una respuesta de error con datos adicionales.
     * 
     * @param message Mensaje de error
     * @param data Datos adicionales del error
     * @return ApiResponse con error y datos
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(
            false, 
            message, 
            data, 
            LocalDateTime.now()
        );
    }
}
