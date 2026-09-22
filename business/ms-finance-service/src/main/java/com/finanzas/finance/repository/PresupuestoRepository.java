package com.finanzas.finance.repository;

import com.finanzas.finance.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Presupuesto.
 * 
 * Proporciona operaciones CRUD y consultas personalizadas sobre la nueva tabla presupuestos
 * que permite gestionar límites financieros por categoría y período.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, UUID> {

    /**
     * Busca todos los presupuestos de un usuario específico.
     * Filtra por user_id para garantizar seguridad de datos.
     */
    List<Presupuesto> findByUserId(UUID userId);

    /**
     * Busca presupuestos de un usuario por categoría.
     */
    List<Presupuesto> findByUserIdAndCategoriaId(UUID userId, UUID categoriaId);

    /**
     * Busca presupuestos de un usuario por año y mes.
     */
    List<Presupuesto> findByUserIdAndAnioAndMes(UUID userId, Integer anio, Integer mes);

    /**
     * Busca presupuestos de un usuario por año.
     */
    List<Presupuesto> findByUserIdAndAnio(UUID userId, Integer anio);

    /**
     * Busca presupuestos de un usuario por año y mes ordenados por categoría.
     */
    List<Presupuesto> findByUserIdAndAnioAndMesOrderByCategoriaIdAsc(UUID userId, Integer anio, Integer mes);

    /**
     * Busca presupuestos de un usuario por categoría, año y mes.
     */
    List<Presupuesto> findByUserIdAndCategoriaIdAndAnioAndMes(
            UUID userId, 
            UUID categoriaId, 
            Integer anio, 
            Integer mes);

    /**
     * Verifica si existe un presupuesto para el mismo usuario, categoría, año y mes.
     * Implementa la restricción única de la base de datos.
     */
    boolean existsByUserIdAndCategoriaIdAndAnioAndMes(
            UUID userId, 
            UUID categoriaId, 
            Integer anio, 
            Integer mes);

    /**
     * Busca un presupuesto por ID y verifica que pertenezca al usuario.
     */
    Optional<Presupuesto> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Lista presupuestos de un usuario ordenados por año descendente, mes descendente y categoría.
     */
    List<Presupuesto> findByUserIdOrderByAnioDescMesDescCategoriaIdAsc(UUID userId);

    /**
     * Verifica si existe un presupuesto para el mismo usuario, categoría, año y mes, excluyendo un ID específico.
     */
    boolean existsByUserIdAndCategoriaIdAndAnioAndMesAndIdNot(
            UUID userId, UUID categoriaId, Integer anio, Integer mes, UUID id);

    /**
     * Verifica si existe un presupuesto para categoría, año, mes de usuario.
     */
    boolean existsByCategoriaIdAndAnioAndMesAndUserId(UUID categoriaId, Integer anio, Integer mes, UUID userId);

    /**
     * Verifica si existe un presupuesto para categoría, año, mes de usuario, excluyendo un ID específico.
     */
    boolean existsByCategoriaIdAndAnioAndMesAndUserIdAndIdNot(
            UUID categoriaId, Integer anio, Integer mes, UUID userId, UUID id);
}
