package com.finanzas.finance.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 * 
 * Se usa para casos donde los datos son válidos pero violan
 * reglas específicas del dominio del negocio.
 * 
 * Ejemplos:
 * - Presupuestos solapados
 * - Categorías con nombres duplicados
 * - Jerarquías con ciclos
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
