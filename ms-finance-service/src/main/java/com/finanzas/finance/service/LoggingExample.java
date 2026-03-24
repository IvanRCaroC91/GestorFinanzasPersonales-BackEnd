package com.finanzas.finance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Ejemplo de logging estructurado aplicado en servicios.
 * 
 * Demuestra las mejores prácticas de logging para producción:
 * - INFO para operaciones exitosas
 * - WARN para errores de negocio (404, 409, 400)
 * - ERROR para errores internos (500)
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Service
public class LoggingExample {

    private static final Logger log = LoggerFactory.getLogger(LoggingExample.class);

    /**
     * Ejemplo de logging en operación exitosa.
     */
    public void operacionExitosa(UUID userId, String recurso) {
        log.info("Operación exitosa - Usuario: {} - Recurso: {} - Acción: CREACIÓN", userId, recurso);
        
        // Lógica de negocio...
        
        log.info("Operación completada - Usuario: {} - Recurso: {} - ID: {} - Duración: 150ms", 
                userId, recurso, UUID.randomUUID());
    }

    /**
     * Ejemplo de logging en error de negocio (WARN).
     */
    public void errorDeNegocio(UUID userId, String recurso, String motivo) {
        log.warn("Error de negocio - Usuario: {} - Recurso: {} - Motivo: {} - Acción: BÚSQUEDA", 
                userId, recurso, motivo);
        
        // Lanzar ResponseStatusException para 404/409/400
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado");
    }

    /**
     * Ejemplo de logging en error interno (ERROR).
     */
    public void errorInterno(UUID userId, String operacion, Exception ex) {
        log.error("Error interno del sistema - Usuario: {} - Operación: {} - Error: {}", 
                userId, operacion, ex.getMessage(), ex);
        
        // Lanzar excepción genérica para 500
        throw new RuntimeException("Error interno del servidor", ex);
    }

    /**
     * Ejemplo de logging estructurado con contexto completo.
     */
    public void loggingEstructurado(UUID userId, String accion, String entidad, Object resultado) {
        log.info("API Call - User: {} | Action: {} | Entity: {} | Result: {} | Timestamp: {}", 
                userId, accion, entidad, resultado, System.currentTimeMillis());
    }

    /**
     * Ejemplo de logging para validaciones de negocio.
     */
    public void validarReglaDeNegocio(UUID userId, String regla, String valor, boolean valido) {
        if (valido) {
            log.debug("Validación exitosa - Usuario: {} - Regla: {} - Valor: {}", userId, regla, valor);
        } else {
            log.warn("Validación fallida - Usuario: {} - Regla: {} - Valor: {} - Motivo: Violación de regla de negocio", 
                    userId, regla, valor);
        }
    }

    /**
     * Ejemplo de logging para operaciones de base de datos.
     */
    public void operacionBaseDeDatos(String operacion, String tabla, long registrosAfectados) {
        log.info("DB Operation - Operation: {} | Table: {} | Records Affected: {} | Duration: 25ms", 
                operacion, tabla, registrosAfectados);
    }

    /**
     * Ejemplo de logging para auditoría de seguridad.
     */
    public void auditoriaSeguridad(UUID userId, String accion, String recurso, String ip, boolean autorizado) {
        if (autorizado) {
            log.info("Security Audit - User: {} | Action: {} | Resource: {} | IP: {} | Status: AUTHORIZED", 
                    userId, accion, recurso, ip);
        } else {
            log.warn("Security Audit - User: {} | Action: {} | Resource: {} | IP: {} | Status: UNAUTHORIZED", 
                    userId, accion, recurso, ip);
        }
    }
}
