package com.finanzas.finance.exception;

import com.finanzas.finance.dto.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la API REST.
 * 
 * Centraliza el manejo de errores y proporciona respuestas
 * consistentes y estructuradas para todos los tipos de excepciones.
 * 
 * Estrategia de logging:
 * - WARN → errores de negocio (404, 409, 400)
 * - ERROR → errores internos del servidor (500)
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de ResponseStatus (404, 409, etc.).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        
        log.warn("ResponseStatusException: {} - {} - {}", 
                ex.getStatusCode(), ex.getReason(), request.getDescription(false));

        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getReason(),
                ex.getStatusCode().value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    /**
     * Maneja excepciones de endpoints no encontrados (404).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        
        log.warn("Endpoint no encontrado: {} {} - {}", 
                ex.getHttpMethod(), ex.getRequestURL(), request.getDescription(false));

        ApiErrorResponse response = ApiErrorResponse.of(
                "Endpoint no encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getRequestURL()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de recursos no encontrados.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.warn("Recurso no encontrado: {} - {}", ex.getMessage(), request.getDescription(false));

        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de reglas de negocio.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        log.warn("Violación de regla de negocio: {} - {}", ex.getMessage(), request.getDescription(false));

        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de acceso no autorizado.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        log.warn("Acceso no autorizado: {} - {}", ex.getMessage(), request.getDescription(false));

        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja excepciones de validación de datos.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.warn("Error de validación: {} - {}", ex.getMessage(), request.getDescription(false));

        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de argumentos inválidos.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Argumento inválido: {} - {}", ex.getMessage(), request.getDescription(false));

        ApiErrorResponse response = ApiErrorResponse.of(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de validación de Spring (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Error de validación de DTO: {} - {}", ex.getMessage(), request.getDescription(false));

        // Extraer mensajes de error de los campos
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse response = ApiErrorResponse.of(
                "Error de validación en los datos de entrada",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de bind de parámetros (@RequestParam inválidos).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        log.warn("Error de tipo de parámetro: {} - {}", ex.getMessage(), request.getDescription(false));

        String message = String.format("El parámetro '%s' tiene un tipo inválido. Se esperaba: %s", 
                ex.getName(), ex.getRequiredType().getSimpleName());

        ApiErrorResponse response = ApiErrorResponse.of(
                message,
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de constraint violation (@Valid).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        log.warn("Error de validación de constraints: {} - {}", ex.getMessage(), request.getDescription(false));

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });

        ApiErrorResponse response = ApiErrorResponse.of(
                "Error de validación en los parámetros",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones genéricas no controladas (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        // Loggear error completo con stacktrace para debugging
        log.error("Error no controlado en la API - URI: {}", 
                request.getDescription(false), ex);

        // NO exponer detalles internos al cliente por seguridad
        ApiErrorResponse response = ApiErrorResponse.of(
                "Error interno del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
