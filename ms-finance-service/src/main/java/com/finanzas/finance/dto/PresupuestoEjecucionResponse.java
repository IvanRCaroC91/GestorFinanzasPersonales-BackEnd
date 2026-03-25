package com.finanzas.finance.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO Response para representar la ejecución financiera de un presupuesto.
 * 
 * Calcula y expone métricas clave del rendimiento de un presupuesto
 * comparando el límite asignado contra los gastos reales del período.
 * 
 * Este DTO es CRÍTICO para el endpoint de ejecución financiera y contiene:
 * - id: Identificador único del presupuesto
 * - categoriaId: ID de la categoría asociada
 * - montoLimite: Límite máximo asignado al presupuesto
 * - totalGastado: Suma real de egresos en el período
 * - disponible: Saldo restante (montoLimite - totalGastado)
 * - porcentajeEjecucion: Porcentaje de utilización (totalGastado / montoLimite * 100)
 * - anio: Año del presupuesto
 * - mes: Mes del presupuesto
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class PresupuestoEjecucionResponse {

    private UUID id;
    private UUID categoriaId;
    private BigDecimal montoLimite;
    private BigDecimal totalGastado;
    private BigDecimal disponible;
    private BigDecimal porcentajeEjecucion;
    private Integer anio;
    private Integer mes;

    // Constructores
    public PresupuestoEjecucionResponse() {}

    // Constructor completo con 8 parámetros en orden exacto
    public PresupuestoEjecucionResponse(
            UUID id,
            UUID categoriaId,
            BigDecimal montoLimite,
            BigDecimal totalGastado,
            BigDecimal disponible,
            BigDecimal porcentajeEjecucion,
            Integer anio,
            Integer mes) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.montoLimite = montoLimite;
        this.totalGastado = totalGastado;
        this.disponible = disponible;
        this.porcentajeEjecucion = porcentajeEjecucion;
        this.anio = anio;
        this.mes = mes;
    }

    // Getters
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

    public BigDecimal getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(BigDecimal totalGastado) {
        this.totalGastado = totalGastado;
    }

    public BigDecimal getDisponible() {
        return disponible;
    }

    public void setDisponible(BigDecimal disponible) {
        this.disponible = disponible;
    }

    public BigDecimal getPorcentajeEjecucion() {
        return porcentajeEjecucion;
    }

    public void setPorcentajeEjecucion(BigDecimal porcentajeEjecucion) {
        this.porcentajeEjecucion = porcentajeEjecucion;
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
