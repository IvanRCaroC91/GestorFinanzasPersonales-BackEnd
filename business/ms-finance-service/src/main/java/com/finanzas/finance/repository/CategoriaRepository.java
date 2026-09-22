package com.finanzas.finance.repository;

import com.finanzas.finance.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Categoria.
 * 
 * Proporciona operaciones CRUD y consultas personalizadas sobre la tabla categorias
 * siguiendo la estructura exacta de la base de datos existente.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Repository  // Indica que esta interfaz es un repositorio de Spring Data JPA
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    /**
     * Busca todas las categorías de un usuario específico.
     * Filtra por user_id para garantizar seguridad de datos y aislamiento entre usuarios.
     * 
     * @param userId ID del usuario cuyas categorías se desean obtener
     * @return Lista de categorías pertenecientes al usuario
     */
    List<Categoria> findByUserId(UUID userId);

    /**
     * Busca categorías de un usuario por tipo (INGRESO/EGRESO).
     * Permite filtrar las categorías según el tipo de movimiento que representan.
     * 
     * @param userId ID del usuario
     * @param tipo Tipo de movimiento (INGRESO o EGRESO)
     * @return Lista de categorías del usuario filtradas por tipo
     */
    List<Categoria> findByUserIdAndTipo(UUID userId, Categoria.TipoMovimiento tipo);

    /**
     * Busca categorías de un usuario por tipo de gasto.
     * Permite filtrar las categorías según el nivel de necesidad del gasto.
     * 
     * @param userId ID del usuario
     * @param tipoGasto Tipo de gasto (NECESARIO, NO NECESARIO, OCASIONAL)
     * @return Lista de categorías del usuario filtradas por tipo de gasto
     */
    List<Categoria> findByUserIdAndTipoGasto(UUID userId, Categoria.TipoGasto tipoGasto);

    /**
     * Busca categorías de un usuario que son categorías padre (sin categoria_padre_id).
     * Retorna las categorías raíz que pueden tener subcategorías hijas.
     * 
     * @param userId ID del usuario
     * @return Lista de categorías padre del usuario
     */
    List<Categoria> findByUserIdAndCategoriaPadreIdIsNull(UUID userId);

    /**
     * Busca categorías hijas de una categoría padre específica.
     */
    List<Categoria> findByCategoriaPadreId(UUID categoriaPadreId);

    /**
     * Verifica si existe una categoría con el mismo nombre para el mismo usuario.
     * Implementa la restricción única (user_id, nombre) de la base de datos.
     */
    boolean existsByUserIdAndNombre(UUID userId, String nombre);

    /**
     * Busca una categoría por ID y verifica que pertenezca al usuario.
     * Método de seguridad para evitar acceso a datos de otros usuarios.
     */
    Optional<Categoria> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Busca categorías de un usuario ordenadas por nombre.
     */
    List<Categoria> findByUserIdOrderByNombreAsc(UUID userId);

    /**
     * Busca categorías de un usuario por tipo ordenadas por nombre.
     */
    List<Categoria> findByUserIdAndTipoOrderByNombreAsc(UUID userId, Categoria.TipoMovimiento tipo);

    /**
     * Verifica si existe una categoría con el mismo nombre para el mismo usuario, excluyendo un ID específico.
     */
    // boolean existsByUserIdAndNombreIgnoreCaseAndIdNot(UUID userId, String nombre, UUID id);
}
