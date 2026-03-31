package com.finanzas.finance.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO Response para representar la ejecución financiera de un presupuesto.
 * 
 * Este DTO contiene las métricas calculadas de ejecución de un presupuesto,
 * comparando el límite asignado contra los gastos reales del período.
 * Es fundamental para el dashboard de control presupuestario.
 * 
 * Campos principales:
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

    // ID único del presupuesto ejecutado.
    private UUID id;
    
    // ID de la categoría asociada al presupuesto.
    private UUID categoriaId;
    
    // Límite máximo asignado al presupuesto.
    private BigDecimal montoLimite;
    
    // Total real gastado en el período.
    private BigDecimal totalGastado;
    
    // Saldo disponible restante (montoLimite - totalGastado).
    private BigDecimal disponible;
    
    // Porcentaje de ejecución del presupuesto (totalGastado / montoLimite * 100).
    private BigDecimal porcentajeEjecucion;
    
    // Año del presupuesto ejecutado.
    private Integer anio;
    
    // Mes del presupuesto ejecutado.
    private Integer mes;

    // Constructor por defecto.
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
