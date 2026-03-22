package com.finanzas.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO Response para representar movimientos financieros.
 * 
 * Expone únicamente los campos necesarios para el frontend,
 * ocultando información sensible y manteniendo compatibilidad
 * con la estructura de la base de datos existente.
 * 
 * Campos mapeados desde la tabla movimientos:
 * - id: UUID primary key
 * - categoriaId: FK a categorias.id
 * - descripcion: TEXT
 * - tipo: ENUM tipo_movimiento
 * - valor: NUMERIC(12,2)
 * - fecha: DATE
 * - createdAt: TIMESTAMP
 * 
 * Nota: facturaId se excluye intencionalmente por ser opcional
 * y no requerirse en las respuestas estándar.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class MovimientoResponse {

    private UUID id;
    private UUID categoriaId;
    private String descripcion;
    private String tipo;
    private BigDecimal valor;
    private LocalDate fecha;
    private LocalDateTime createdAt;

    // Constructores
    public MovimientoResponse() {}

    // Constructor de conveniencia para mapeo desde entidad
    public MovimientoResponse(UUID id, UUID categoriaId, String descripcion, 
                              String tipo, BigDecimal valor, LocalDate fecha, 
                              LocalDateTime createdAt) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = fecha;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
