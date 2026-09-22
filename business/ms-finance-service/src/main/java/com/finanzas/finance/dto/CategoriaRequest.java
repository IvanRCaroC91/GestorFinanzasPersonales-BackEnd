package com.finanzas.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO Request para crear y actualizar categorías financieras.
 * 
 * Este DTO contiene los datos que el frontend envía cuando
 * desea crear o modificar una categoría en el sistema.
 * 
 * Campos según la tabla categorias:
 * - nombre: TEXT NOT NULL
 * - tipo: ENUM tipo_movimiento ('INGRESO', 'EGRESO')
 * - tipo_gasto: ENUM tipo_gasto ('NECESARIO', 'NO NECESARIO', 'OCASIONAL')
 * - categoria_padre_id: FK a categorias.id (nullable)
 * 
 * user_id se gestiona internamente para mantener seguridad de datos.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
public class CategoriaRequest {

    // Nombre de la categoría (obligatorio, único por usuario).
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    // Tipo de movimiento: INGRESO o EGRESO (obligatorio).
    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "INGRESO|EGRESO", message = "El tipo debe ser INGRESO o EGRESO")
    private String tipo;

    // Tipo de gasto: NECESARIO, NO_NECESARIO u OCASIONAL (obligatorio).
    @NotNull(message = "El tipo de gasto es obligatorio")
    @Pattern(regexp = "NECESARIO|NO_NECESARIO|OCASIONAL", 
             message = "El tipo de gasto debe ser NECESARIO, NO_NECESARIO u OCASIONAL")
    private String tipoGasto = "NECESARIO";

    // ID de la categoría padre (opcional, para subcategorías).
    private UUID categoriaPadreId;

    // Constructor por defecto que establece el tipo de gasto inicial.
    public CategoriaRequest() {
        this.tipoGasto = "NECESARIO"; // Valor por defecto según BD
    }

    // Getters y Setters
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

    public UUID getCategoriaPadreId() {
        return categoriaPadreId;
    }

    public void setCategoriaPadreId(UUID categoriaPadreId) {
        this.categoriaPadreId = categoriaPadreId;
    }
}
