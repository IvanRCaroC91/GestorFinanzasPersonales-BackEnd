package com.finanzas.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * - periodoInicio: DATE
 * - periodoFin: DATE
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
    private LocalDate periodoInicio;
    private LocalDate periodoFin;

    // Constructores
    public PresupuestoResponse() {}

    // Constructor de conveniencia para mapeo desde entidad
    public PresupuestoResponse(UUID id, UUID categoriaId, BigDecimal montoLimite,
                               LocalDate periodoInicio, LocalDate periodoFin) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.montoLimite = montoLimite;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
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

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }
}
