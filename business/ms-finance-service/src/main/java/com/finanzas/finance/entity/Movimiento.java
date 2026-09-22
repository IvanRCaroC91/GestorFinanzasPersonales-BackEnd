package com.finanzas.finance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa la tabla 'movimientos' de la base de datos.
 * 
 * Mapea directamente la estructura de la tabla movimientos existente:
 * - id: UUID primary key
 * - user_id: FK a usuarios.id
 * - categoria_id: FK a categorias.id  
 * - factura_id: FK a facturas.id (nullable)
 * - descripcion: TEXT
 * - tipo: ENUM tipo_movimiento ('INGRESO', 'EGRESO')
 * - valor: NUMERIC(12,2)
 * - fecha: DATE
 * - created_at: TIMESTAMP
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @GeneratedValue(strategy = GenerationType.AUTO) // PostgreSQL gen_random_uuid()
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "categoria_id", nullable = false, columnDefinition = "UUID")
    private UUID categoriaId;

    @Column(name = "factura_id", columnDefinition = "UUID")
    private UUID facturaId;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Mapeo del ENUM PostgreSQL tipo_movimiento
     * Valores: 'INGRESO', 'EGRESO'
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, columnDefinition = "tipo_movimiento")
    private TipoMovimiento tipo;

    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enum que mapea el tipo personalizado de PostgreSQL
    public enum TipoMovimiento {
        INGRESO,
        EGRESO
    }

    // Constructores
    public Movimiento() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(UUID categoriaId) {
        this.categoriaId = categoriaId;
    }

    public UUID getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(UUID facturaId) {
        this.facturaId = facturaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
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
