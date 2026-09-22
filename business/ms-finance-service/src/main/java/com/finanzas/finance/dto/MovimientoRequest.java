package com.finanzas.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO Request para crear y actualizar movimientos financieros.
 * 
 * Mapea los campos necesarios para operaciones CRUD sobre la tabla movimientos,
 * respetando la estructura exacta de la base de datos existente.
 * 
 * Campos obligatorios según la tabla:
 * - categoria_id: FK a categorias.id
 * - descripcion: TEXT NOT NULL  
 * - tipo: ENUM tipo_movimiento ('INGRESO', 'EGRESO')
 * - valor: NUMERIC(12,2) NOT NULL CHECK (valor >= 0)
 * - fecha: DATE NOT NULL
 * - factura_id: FK a facturas.id (nullable)
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class MovimientoRequest {

    @NotNull(message = "El ID de la categoría es obligatorio")
    private UUID categoriaId;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "INGRESO|EGRESO", message = "El tipo debe ser INGRESO o EGRESO")
    private String tipo;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a cero")
    private BigDecimal valor;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private UUID facturaId; // Opcional

    // Constructores
    public MovimientoRequest() {}

    // Getters y Setters
    public UUID getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(UUID categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public UUID getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(UUID facturaId) {
        this.facturaId = facturaId;
    }
}
