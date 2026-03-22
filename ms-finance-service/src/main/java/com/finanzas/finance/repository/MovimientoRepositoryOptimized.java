package com.finanzas.finance.repository;

import com.finanzas.finance.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA optimizado para la entidad Movimiento.
 * 
 * Mejoras implementadas:
 * - Métodos seguros que siempre filtran por user_id
 * - Consultas optimizadas para evitar N+1
 * - Nombres descriptivos y consistentes
 * 
 * @author Sistema de Finanzas Personales
 * @version 2.0.0
 */
@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, UUID> {

    /**
     * Busca un movimiento por ID validando que pertenezca al usuario.
     * Método seguro que previene acceso a datos de otros usuarios.
     */
    Optional<Movimiento> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Lista todos los movimientos de un usuario específico.
     * Filtra por user_id para garantizar seguridad de datos.
     */
    List<Movimiento> findByUserId(UUID userId);

    /**
     * Busca movimientos de un usuario por tipo (INGRESO/EGRESO).
     */
    List<Movimiento> findByUserIdAndTipo(UUID userId, Movimiento.TipoMovimiento tipo);

    /**
     * Busca movimientos de un usuario en un rango de fechas.
     */
    List<Movimiento> findByUserIdAndFechaBetween(UUID userId, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca movimientos de un usuario por categoría.
     */
    List<Movimiento> findByUserIdAndCategoriaId(UUID userId, UUID categoriaId);

    /**
     * Calcula el total gastado por un usuario en una categoría específica
     * dentro de un período determinado.
     * 
     * Esta consulta es crítica para el endpoint de ejecución de presupuestos.
     * Filtra solo movimientos de tipo EGRESO.
     */
    @Query("SELECT COALESCE(SUM(m.valor), 0) " +
           "FROM Movimiento m " +
           "WHERE m.userId = :userId " +
           "AND m.categoriaId = :categoriaId " +
           "AND m.tipo = 'EGRESO' " +
           "AND m.fecha BETWEEN :periodoInicio AND :periodoFin")
    BigDecimal sumGastosByUsuarioAndCategoriaAndPeriodo(
            @Param("userId") UUID userId,
            @Param("categoriaId") UUID categoriaId,
            @Param("periodoInicio") LocalDate periodoInicio,
            @Param("periodoFin") LocalDate periodoFin);

    /**
     * Calcula el total de ingresos de un usuario en un período.
     */
    @Query("SELECT COALESCE(SUM(m.valor), 0) " +
           "FROM Movimiento m " +
           "WHERE m.userId = :userId " +
           "AND m.tipo = 'INGRESO' " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumIngresosByUsuarioAndPeriodo(
            @Param("userId") UUID userId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Calcula el total de egresos de un usuario en un período.
     */
    @Query("SELECT COALESCE(SUM(m.valor), 0) " +
           "FROM Movimiento m " +
           "WHERE m.userId = :userId " +
           "AND m.tipo = 'EGRESO' " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumEgresosByUsuarioAndPeriodo(
            @Param("userId") UUID userId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
