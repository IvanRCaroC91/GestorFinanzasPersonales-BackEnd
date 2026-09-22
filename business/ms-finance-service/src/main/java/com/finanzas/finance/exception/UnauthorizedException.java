package com.finanzas.finance.exception;

/**
 * Excepción lanzada cuando un usuario intenta acceder o modificar
 * un recurso que no le pertenece.
 * 
 * Se usa para violaciones de seguridad donde un usuario autenticado
 * intenta operar sobre datos de otro usuario.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
