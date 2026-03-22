package com.finanzas.finance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO Request para crear y actualizar presupuestos financieros.
 * 
 * Mapea los campos necesarios para operaciones CRUD sobre la tabla presupuestos,
 * respetando la estructura exacta de la base de datos existente.
 * 
 * Campos según la tabla presupuestos:
 * - categoria_id: FK a categorias.id
 * - monto_limite: NUMERIC(12,2) NOT NULL CHECK (monto_limite >= 0)
 * - periodo_inicio: DATE NOT NULL
 * - periodo_fin: DATE NOT NULL
 * 
 * Validaciones críticas implementadas:
 * - periodo_inicio < periodo_fin (validado en BD con constraint)
 * - monto_limite >= 0 (validado en BD y aquí)
 * - categoría pertenece al usuario (validado en service)
 * - sin solapamiento de períodos (validado en service)
 * 
 * user_id se gestiona internamente para mantener seguridad de datos.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class PresupuestoRequest {

    @NotNull(message = "El ID de la categoría es obligatorio")
    private UUID categoriaId;

    @NotNull(message = "El monto límite es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto límite debe ser mayor a cero")
    private BigDecimal montoLimite;

    @NotNull(message = "La fecha de inicio del período es obligatoria")
    private LocalDate periodoInicio;

    @NotNull(message = "La fecha de fin del período es obligatoria")
    @FutureOrPresent(message = "La fecha de fin debe ser presente o futura")
    private LocalDate periodoFin;

    // Constructores
    public PresupuestoRequest() {}

    // Getters y Setters
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
