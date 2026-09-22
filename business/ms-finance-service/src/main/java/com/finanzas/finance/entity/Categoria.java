package com.finanzas.finance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa la tabla 'categorias' de la base de datos.
 * 
 * Mapea directamente la estructura de la tabla categorias existente:
 * - id: UUID primary key
 * - user_id: FK a usuarios.id
 * - nombre: TEXT
 * - tipo: ENUM tipo_movimiento ('INGRESO', 'EGRESO')
 * - tipo_gasto: ENUM tipo_gasto ('NECESARIO', 'NO NECESARIO', 'OCASIONAL')
 * - categoria_padre_id: FK a categorias.id (nullable, auto-referencia)
 * - created_at: TIMESTAMP
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    // Identificador único de la categoría (UUID).
    // Se genera automáticamente usando gen_random_uuid() de PostgreSQL.
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @GeneratedValue(strategy = GenerationType.AUTO) // PostgreSQL gen_random_uuid()
    private UUID id;

    // ID del usuario dueño de la categoría.
    // Garantiza el aislamiento de datos entre usuarios.
    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    // Nombre descriptivo de la categoría.
    // No puede ser nulo y debe ser único por usuario.
    @Column(name = "nombre", nullable = false, columnDefinition = "TEXT")
    private String nombre;

    /**
     * Mapeo del ENUM PostgreSQL tipo_movimiento.
     * Define si la categoría es para ingresos o egresos.
     * Valores: 'INGRESO', 'EGRESO'
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, columnDefinition = "tipo_movimiento")
    private TipoMovimiento tipo;

    /**
     * Mapeo del ENUM PostgreSQL tipo_gasto.
     * Clasifica los egresos por nivel de necesidad.
     * Valores: 'NECESARIO', 'NO NECESARIO', 'OCASIONAL'
     * Default: 'NECESARIO'
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_gasto", nullable = false, columnDefinition = "tipo_gasto")
    private TipoGasto tipoGasto = TipoGasto.NECESARIO;

    /**
     * Auto-referencia para categorías padre/hijo
     * FK a categorias.id, permite NULL para categorías raíz
     */
    @Column(name = "categoria_padre_id", columnDefinition = "UUID")
    private UUID categoriaPadreId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums que mapean los tipos personalizados de PostgreSQL
    public enum TipoMovimiento {
        INGRESO,
        EGRESO
    }

    public enum TipoGasto {
        NECESARIO,
        NO_NECESARIO,
        OCASIONAL
    }

    // Constructores
    public Categoria() {
        this.createdAt = LocalDateTime.now();
        this.tipoGasto = TipoGasto.NECESARIO;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public TipoGasto getTipoGasto() {
        return tipoGasto;
    }

    public void setTipoGasto(TipoGasto tipoGasto) {
        this.tipoGasto = tipoGasto;
    }

    public UUID getCategoriaPadreId() {
        return categoriaPadreId;
    }

    public void setCategoriaPadreId(UUID categoriaPadreId) {
        this.categoriaPadreId = categoriaPadreId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
