package com.finanzas.finance.exception;

import com.finanzas.finance.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la API REST.
 * 
 * Centraliza el manejo de errores y proporciona respuestas
 * consistentes y estructuradas para todos los tipos de excepciones.
 * 
 * Estrategia de manejo de errores:
 * - ResourceNotFoundException → 404 NOT FOUND
 * - BusinessException → 400 BAD REQUEST  
 * - UnauthorizedException → 403 FORBIDDEN
 * - ValidationException → 400 BAD REQUEST
 * - Exception genérica → 500 INTERNAL SERVER ERROR
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de recursos no encontrados.
     * 
     * @param ex Excepción capturada
     * @param request Contexto del request
     * @return Respuesta con error 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de reglas de negocio.
     * 
     * @param ex Excepción capturada
     * @param request Contexto del request
     * @return Respuesta con error 400
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        log.warn("Violación de regla de negocio: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de acceso no autorizado.
     * 
     * @param ex Excepción capturada
     * @param request Contexto del request
     * @return Respuesta con error 403
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        log.warn("Acceso no autorizado: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja excepciones de validación de datos.
     * 
     * @param ex Excepción capturada
     * @param request Contexto del request
     * @return Respuesta con error 400
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.warn("Error de validación: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de argumentos inválidos (IllegalArgumentException).
     * 
     * @param ex Excepción capturada
     * @param request Contexto del request
     * @return Respuesta con error 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Argumento inválido: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de validación de Spring (@Valid).
     * 
     * @param ex Excepción de validación
     * @param request Contexto del request
     * @return Respuesta con error 400 y detalles de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Error de validación de DTO: {}", ex.getMessage());
        
        // Extraer mensajes de error de los campos
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ApiResponse<Object> response = ApiResponse.error("Error de validación en los datos de entrada", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de bind de parámetros (@RequestParam inválidos).
     * 
     * @param ex Excepción de bind
     * @param request Contexto del request
     * @return Respuesta con error 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        log.warn("Error de tipo de parámetro: {}", ex.getMessage());
        
        String message = String.format("El parámetro '%s' tiene un tipo inválido. Se esperaba: %s", 
                ex.getName(), ex.getRequiredType().getSimpleName());
        
        ApiResponse<Object> response = ApiResponse.error(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de constraint violation (@Valid).
     * 
     * @param ex Excepción de constraint
     * @param request Contexto del request
     * @return Respuesta con error 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        log.warn("Error de validación de constraints: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });
        
        ApiResponse<Object> response = ApiResponse.error("Error de validación en los parámetros", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones genéricas no controladas.
     * 
     * @param ex Excepción capturada
     * @param request Contexto del request
     * @return Respuesta con error 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        // Loggear error completo con stacktrace para debugging
        log.error("Error no controlado en la API - URI: {}", 
                request.getDescription(false), ex);
        
        // NO exponer detalles internos al cliente por seguridad
        ApiResponse<Object> response = ApiResponse.error("Error interno del servidor");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
