package com.finanzas.finance.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO Response para representar categorías financieras.
 * 
 * Expone únicamente los campos necesarios para el frontend,
 * manteniendo compatibilidad con la estructura de la base de datos
 * y ocultando información sensible como user_id.
 * 
 * Campos mapeados desde la tabla categorias:
 * - id: UUID primary key
 * - nombre: TEXT
 * - tipo: ENUM tipo_movimiento
 * - tipoGasto: ENUM tipo_gasto
 * 
 * Nota: categoriaPadreId y createdAt se excluyen intencionalmente
 * para mantener respuestas limpias y enfocadas en los datos de negocio.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class CategoriaResponse {

    private UUID id;
    private String nombre;
    private String tipo;
    private String tipoGasto;

    // Constructores
    public CategoriaResponse() {}

    // Constructor de conveniencia para mapeo desde entidad
    public CategoriaResponse(UUID id, String nombre, String tipo, String tipoGasto) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.tipoGasto = tipoGasto;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoGasto() {
        return tipoGasto;
    }

    public void setTipoGasto(String tipoGasto) {
        this.tipoGasto = tipoGasto;
    }
}
