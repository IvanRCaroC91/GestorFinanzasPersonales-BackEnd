package com.finanzas.finance.exception;

/**
 * Excepción lanzada cuando un recurso no es encontrado.
 * 
 * Se usa para casos donde un ID no existe en la base de datos
 * o cuando un recurso no pertenece al usuario solicitante.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
