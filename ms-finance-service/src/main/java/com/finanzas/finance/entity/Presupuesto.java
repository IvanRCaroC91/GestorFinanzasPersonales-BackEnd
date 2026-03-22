package com.finanzas.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
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
 * - periodo_inicio: DATE (inicio del período presupuestario)
 * - periodo_fin: DATE (fin del período presupuestario)
 * - created_at: TIMESTAMP
 * 
 * Restricción única: (user_id, categoria_id, periodo_inicio, periodo_fin)
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Entity
@Table(name = "presupuestos", 
       uniqueConstraints = {
           @UniqueConstraint(
               name = "presupuestos_usuario_categoria_periodo_unique",
               columnNames = {"user_id", "categoria_id", "periodo_inicio", "periodo_fin"}
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

    @NotNull(message = "La fecha de inicio del período es obligatoria")
    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @NotNull(message = "La fecha de fin del período es obligatoria")
    @FutureOrPresent(message = "La fecha de fin del período debe ser presente o futura")
    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
