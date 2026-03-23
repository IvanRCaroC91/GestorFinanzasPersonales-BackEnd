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
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    /**
     * Busca todas las categorías de un usuario específico.
     * Filtra por user_id para garantizar seguridad de datos.
     */
    List<Categoria> findByUserId(UUID userId);

    /**
     * Busca categorías de un usuario por tipo (INGRESO/EGRESO).
     */
    List<Categoria> findByUserIdAndTipo(UUID userId, Categoria.TipoMovimiento tipo);

    /**
     * Busca categorías de un usuario por tipo de gasto.
     */
    List<Categoria> findByUserIdAndTipoGasto(UUID userId, Categoria.TipoGasto tipoGasto);

    /**
     * Busca categorías de un usuario que son categorías padre (sin categoria_padre_id).
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
