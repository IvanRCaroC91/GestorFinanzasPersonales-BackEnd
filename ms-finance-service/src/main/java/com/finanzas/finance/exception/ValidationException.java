package com.finanzas.finance.exception;

/**
 * Excepción lanzada cuando los datos de entrada son inválidos.
 * 
 * Se usa para errores de validación de datos que no cumplen
 * con las reglas básicas de formato o valores permitidos.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
