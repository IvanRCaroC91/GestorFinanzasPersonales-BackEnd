package com.finanzas.finance.repository;

import com.finanzas.finance.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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
     * Busca presupuestos activos de un usuario en una fecha específica.
     * Un presupuesto está activo si la fecha está dentro del período.
     */
    @Query("SELECT p FROM Presupuesto p " +
           "WHERE p.userId = :userId " +
           "AND :fecha BETWEEN p.periodoInicio AND p.periodoFin")
    List<Presupuesto> findActivosByUsuarioAndFecha(
            @Param("userId") UUID userId, 
            @Param("fecha") LocalDate fecha);

    /**
     * Busca presupuestos de un usuario en un rango de fechas.
     */
    @Query("SELECT p FROM Presupuesto p " +
           "WHERE p.userId = :userId " +
           "AND p.periodoInicio <= :fechaFin " +
           "AND p.periodoFin >= :fechaInicio")
    List<Presupuesto> findByUsuarioAndRangoFechas(
            @Param("userId") UUID userId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca presupuestos de un usuario por categoría y período.
     */
    List<Presupuesto> findByUserIdAndCategoriaIdAndPeriodoInicioAndPeriodoFin(
            UUID userId, 
            UUID categoriaId, 
            LocalDate periodoInicio, 
            LocalDate periodoFin);

    /**
     * Verifica si existe un presupuesto para el mismo usuario, categoría y período.
     * Implementa la restricción única de la base de datos.
     */
    boolean existsByUserIdAndCategoriaIdAndPeriodoInicioAndPeriodoFin(
            UUID userId, 
            UUID categoriaId, 
            LocalDate periodoInicio, 
            LocalDate periodoFin);

    /**
     * Busca un presupuesto por ID y verifica que pertenezca al usuario.
     * Método de seguridad para evitar acceso a datos de otros usuarios.
     */
    Presupuesto findByIdAndUserId(UUID id, UUID userId);

    /**
     * Busca presupuestos que overlapan con un nuevo período.
     * Útil para validación antes de crear nuevos presupuestos.
     */
    @Query("SELECT p FROM Presupuesto p " +
           "WHERE p.userId = :userId " +
           "AND p.categoriaId = :categoriaId " +
           "AND ((p.periodoInicio <= :periodoFin AND p.periodoFin >= :periodoInicio))")
    List<Presupuesto> findOverlappingPeriodos(
            @Param("userId") UUID userId,
            @Param("categoriaId") UUID categoriaId,
            @Param("periodoInicio") LocalDate periodoInicio,
            @Param("periodoFin") LocalDate periodoFin);
}
