package com.finanzas.finance.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO Response para representar presupuestos financieros.
 * 
 * Expone únicamente los campos necesarios para el frontend,
 * manteniendo compatibilidad con la estructura de la base de datos
 * y ocultando información sensible como user_id.
 * 
 * Campos mapeados desde la tabla presupuestos:
 * - id: UUID primary key
 * - categoriaId: FK a categorias.id
 * - montoLimite: NUMERIC(12,2)
 * - anio: INT (2000-2100)
 * - mes: INT (1-12)
 * 
 * Nota: createdAt se excluye intencionalmente para mantener
 * respuestas limpias y enfocadas en los datos de negocio.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class PresupuestoResponse {

    private UUID id;
    private UUID categoriaId;
    private BigDecimal montoLimite;
    private Integer anio;
    private Integer mes;

    // Constructores
    public PresupuestoResponse() {}

    // Constructor de conveniencia para mapeo desde entidad
    public PresupuestoResponse(UUID id, UUID categoriaId, BigDecimal montoLimite,
                               Integer anio, Integer mes) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.montoLimite = montoLimite;
        this.anio = anio;
        this.mes = mes;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(UUID categoriaId) {
        this.categoriaId = categoriaId;
    }

    public BigDecimal getMontoLimite() {
        return montoLimite;
    }

    public void setMontoLimite(BigDecimal montoLimite) {
        this.montoLimite = montoLimite;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }
}
