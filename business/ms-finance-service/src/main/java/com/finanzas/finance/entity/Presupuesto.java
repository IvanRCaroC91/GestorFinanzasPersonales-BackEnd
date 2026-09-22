package com.finanzas.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa la tabla 'presupuestos' de la base de datos.
 * 
 * Nueva tabla agregada para gestionar límites presupuestarios por categoría y período:
 * - id: UUID primary key
 * - user_id: FK a usuarios.id
 * - categoria_id: FK a categorias.id
 * - monto_limite: NUMERIC(12,2) con CHECK >= 0
 * - anio: INT (año del presupuesto, 2000-2100)
 * - mes: INT (mes del presupuesto, 1-12)
 * - created_at: TIMESTAMP
 * 
 * Restricción única: (user_id, categoria_id, anio, mes)
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Entity
@Table(name = "presupuestos", 
       uniqueConstraints = {
           @UniqueConstraint(
               name = "presupuestos_usuario_categoria_mes_anio_unique",
               columnNames = {"user_id", "categoria_id", "anio", "mes"}
           )
       })
public class Presupuesto {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @GeneratedValue(strategy = GenerationType.AUTO) // PostgreSQL gen_random_uuid()
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "categoria_id", nullable = false, columnDefinition = "UUID")
    private UUID categoriaId;

    @NotNull(message = "El monto límite es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto límite debe ser mayor o igual a cero")
    @Column(name = "monto_limite", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoLimite;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año debe ser mayor o igual a 2000")
    @Max(value = 2100, message = "El año debe ser menor o igual a 2100")
    @Column(name = "anio", nullable = false)
    private Integer anio;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructores
    public Presupuesto() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
