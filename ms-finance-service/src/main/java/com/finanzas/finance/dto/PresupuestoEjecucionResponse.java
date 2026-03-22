package com.finanzas.finance.dto;

import java.math.BigDecimal;

/**
 * DTO Response para representar la ejecución financiera de un presupuesto.
 * 
 * Calcula y expone métricas clave del rendimiento de un presupuesto
 * comparando el límite asignado contra los gastos reales del período.
 * 
 * Este DTO es CRÍTICO para el endpoint de ejecución financiera y contiene:
 * - montoLimite: Límite máximo asignado al presupuesto
 * - totalGastado: Suma real de egresos en el período
 * - disponible: Saldo restante (montoLimite - totalGastado)
 * - porcentajeUsado: Porcentaje de utilización (totalGastado / montoLimite * 100)
 * 
 * Lógica de cálculo implementada en PresupuestoService:
 * 1. Obtiene presupuesto por ID y usuario
 * 2. Consulta movimientos WHERE:
 *    - tipo = 'EGRESO'
 *    - categoria_id = presupuesto.categoria_id  
 *    - fecha BETWEEN periodo_inicio AND periodo_fin
 *    - user_id = usuario autenticado
 * 3. Calcula métricas de ejecución
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class PresupuestoEjecucionResponse {

    private BigDecimal montoLimite;
    private BigDecimal totalGastado;
    private BigDecimal disponible;
    private BigDecimal porcentajeUsado;

    // Constructores
    public PresupuestoEjecucionResponse() {}

    // Constructor de conveniencia para cálculos
    public PresupuestoEjecucionResponse(BigDecimal montoLimite, BigDecimal totalGastado) {
        this.montoLimite = montoLimite;
        this.totalGastado = totalGastado;
        this.disponible = montoLimite.subtract(totalGastado);
        
        // Calcular porcentaje usado con protección contra división por cero
        if (montoLimite.compareTo(BigDecimal.ZERO) > 0) {
            this.porcentajeUsado = totalGastado
                .divide(montoLimite, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        } else {
            this.porcentajeUsado = BigDecimal.ZERO;
        }
    }

    // Getters y Setters
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

    public BigDecimal getPorcentajeUsado() {
        return porcentajeUsado;
    }

    public void setPorcentajeUsado(BigDecimal porcentajeUsado) {
        this.porcentajeUsado = porcentajeUsado;
    }
}
