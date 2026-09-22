package com.finanzas.finance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
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
 * - anio: INT NOT NULL (2000-2100)
 * - mes: INT NOT NULL (1-12)
 * 
 * Validaciones críticas implementadas:
 * - anio entre 2000 y 2100
 * - mes entre 1 y 12
 * - monto_limite >= 0 (validado en BD y aquí)
 * - categoría pertenece al usuario (validado en service)
 * - sin duplicados por (user, categoria, mes, anio) (validado en service)
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

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año debe ser mayor o igual a 2000")
    @Max(value = 2100, message = "El año debe ser menor o igual a 2100")
    private Integer anio;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer mes;

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
