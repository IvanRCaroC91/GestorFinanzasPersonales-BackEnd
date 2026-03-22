package com.finanzas.finance.service;

import com.finanzas.finance.dto.PresupuestoRequest;
import com.finanzas.finance.dto.PresupuestoResponse;
import com.finanzas.finance.dto.PresupuestoEjecucionResponse;
import com.finanzas.finance.entity.Presupuesto;
import com.finanzas.finance.entity.Categoria;
import com.finanzas.finance.repository.PresupuestoRepository;
import com.finanzas.finance.repository.CategoriaRepository;
import com.finanzas.finance.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service de negocio para gestión de presupuestos financieros.
 * 
 * Implementa toda la lógica de negocio para operaciones CRUD sobre la tabla presupuestos,
 * garantizando integridad de datos, seguridad por usuario y validaciones críticas.
 * 
 * Validaciones implementadas:
 * - La categoría debe existir y pertenecer al usuario
 * - periodo_inicio < periodo_fin (validado en BD y aquí)
 * - El monto límite debe ser mayor a cero
 * - Evitar solapamiento de presupuestos por categoría y período
 * - user_id siempre aplicado para seguridad de datos
 * 
 * VALIDACIÓN CRÍTICA - Solapamiento de presupuestos:
 * Se impide crear presupuestos que overlappen con existentes para la misma categoría:
 * nuevo.periodo_inicio <= existente.periodo_fin AND nuevo.periodo_fin >= existente.periodo_inicio
 * 
 * Relaciones con otras tablas:
 * - presupuestos.categoria_id → categorias.id (obligatorio)
 * - presupuestos.user_id → usuarios.id (seguridad)
 * - movimientos → calculados para ejecución financiera
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Service
@Transactional
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoRepository movimientoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository,
                             CategoriaRepository categoriaRepository,
                             MovimientoRepository movimientoRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.categoriaRepository = categoriaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Crea un nuevo presupuesto con validaciones completas incluyendo anti-solapamiento.
     * 
     * @param request Datos del presupuesto a crear
     * @param userId ID del usuario autenticado
     * @return PresupuestoResponse con los datos guardados
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public PresupuestoResponse crearPresupuesto(PresupuestoRequest request, UUID userId) {
        // Validar que la categoría exista y pertenezca al usuario
        Categoria categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
            .orElseThrow(() -> new IllegalArgumentException(
                "La categoría no existe o no pertenece al usuario"));

        // Validar que la categoría sea de tipo EGRESO (solo estos tienen presupuestos)
        if (!categoria.getTipo().name().equals("EGRESO")) {
            throw new IllegalArgumentException(
                "Solo se pueden crear presupuestos para categorías de egresos");
        }

        // Validar lógica de fechas
        if (!request.getPeriodoInicio().isBefore(request.getPeriodoFin())) {
            throw new IllegalArgumentException(
                "La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // VALIDACIÓN CRÍTICA: Evitar solapamiento de presupuestos
        List<Presupuesto> solapados = presupuestoRepository.findOverlappingPeriodos(
            userId, 
            request.getCategoriaId(), 
            request.getPeriodoInicio(), 
            request.getPeriodoFin()
        );

        if (!solapados.isEmpty()) {
            throw new IllegalArgumentException(
                "Ya existe un presupuesto para esta categoría en el período especificado");
        }

        // Crear entidad
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setUserId(userId);
        presupuesto.setCategoriaId(request.getCategoriaId());
        presupuesto.setMontoLimite(request.getMontoLimite());
        presupuesto.setPeriodoInicio(request.getPeriodoInicio());
        presupuesto.setPeriodoFin(request.getPeriodoFin());

        // Guardar y retornar respuesta
        Presupuesto guardado = presupuestoRepository.save(presupuesto);
        return mapToResponse(guardado);
    }

    /**
     * Actualiza un presupuesto existente con validaciones de seguridad y anti-solapamiento.
     * 
     * @param id ID del presupuesto a actualizar
     * @param request Nuevos datos del presupuesto
     * @param userId ID del usuario autenticado
     * @return PresupuestoResponse actualizado
     * @throws IllegalArgumentException si no existe o las validaciones fallan
     */
    public PresupuestoResponse actualizarPresupuesto(UUID id, PresupuestoRequest request, UUID userId) {
        // Buscar presupuesto existente y validar propiedad
        Presupuesto existente = presupuestoRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("El presupuesto no existe o no pertenece al usuario"));

        // Validar categoría si cambia
        if (!request.getCategoriaId().equals(existente.getCategoriaId())) {
            Categoria categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "La categoría no existe o no pertenece al usuario"));

            if (!categoria.getTipo().name().equals("EGRESO")) {
                throw new IllegalArgumentException(
                    "Solo se pueden crear presupuestos para categorías de egresos");
            }
        }

        // Validar lógica de fechas
        if (!request.getPeriodoInicio().isBefore(request.getPeriodoFin())) {
            throw new IllegalArgumentException(
                "La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // VALIDACIÓN CRÍTICA: Evitar solapamiento (excluyendo el presupuesto actual)
        List<Presupuesto> solapados = presupuestoRepository.findOverlappingPeriodos(
            userId, 
            request.getCategoriaId(), 
            request.getPeriodoInicio(), 
            request.getPeriodoFin()
        );

        // Remover el presupuesto actual de la lista de solapados
        solapados = solapados.stream()
            .filter(p -> !p.getId().equals(id))
            .collect(Collectors.toList());

        if (!solapados.isEmpty()) {
            throw new IllegalArgumentException(
                "Ya existe otro presupuesto para esta categoría en el período especificado");
        }

        // Actualizar campos
        existente.setCategoriaId(request.getCategoriaId());
        existente.setMontoLimite(request.getMontoLimite());
        existente.setPeriodoInicio(request.getPeriodoInicio());
        existente.setPeriodoFin(request.getPeriodoFin());

        Presupuesto actualizado = presupuestoRepository.save(existente);
        return mapToResponse(actualizado);
    }

    /**
     * Elimina un presupuesto verificando propiedad del usuario.
     * 
     * @param id ID del presupuesto a eliminar
     * @param userId ID del usuario autenticado
     * @throws IllegalArgumentException si no existe o no pertenece al usuario
     */
    public void eliminarPresupuesto(UUID id, UUID userId) {
        Presupuesto presupuesto = presupuestoRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("El presupuesto no existe o no pertenece al usuario"));

        presupuestoRepository.delete(presupuesto);
    }

    /**
     * Lista todos los presupuestos de un usuario específico.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de presupuestos del usuario
     */
    @Transactional(readOnly = true)
    public List<PresupuestoResponse> listarPresupuestos(UUID userId) {
        List<Presupuesto> presupuestos = presupuestoRepository.findByUserId(userId);
        return presupuestos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un presupuesto específico por ID validando propiedad del usuario.
     * 
     * @param id ID del presupuesto a buscar
     * @param userId ID del usuario autenticado
     * @return PresupuestoResponse encontrado
     * @throws IllegalArgumentException si no existe o no pertenece al usuario
     */
    @Transactional(readOnly = true)
    public PresupuestoResponse obtenerPresupuestoPorId(UUID id, UUID userId) {
        Presupuesto presupuesto = presupuestoRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("El presupuesto no existe o no pertenece al usuario"));

        return mapToResponse(presupuesto);
    }

    /**
     * Calcula la ejecución financiera de un presupuesto.
     * 
     * Este método es CRÍTICO y calcula métricas clave del rendimiento del presupuesto:
     * 1. Obtiene presupuesto por ID y usuario
     * 2. Consulta movimientos de egresos en el período
     * 3. Calcula total gastado, disponible y porcentaje usado
     * 
     * @param presupuestoId ID del presupuesto a evaluar
     * @param userId ID del usuario autenticado
     * @return PresupuestoEjecucionResponse con métricas de ejecución
     * @throws IllegalArgumentException si no existe o no pertenece al usuario
     */
    @Transactional(readOnly = true)
    public PresupuestoEjecucionResponse calcularEjecucionPresupuesto(UUID presupuestoId, UUID userId) {
        // Obtener presupuesto y validar propiedad
        Presupuesto presupuesto = presupuestoRepository.findByIdAndUserId(presupuestoId, userId)
            .orElseThrow(() -> new IllegalArgumentException("El presupuesto no existe o no pertenece al usuario"));

        // Calcular total gastado usando query optimizada
        BigDecimal totalGastado = movimientoRepository.sumGastosByUsuarioAndCategoriaAndPeriodo(
            userId,
            presupuesto.getCategoriaId(),
            presupuesto.getPeriodoInicio(),
            presupuesto.getPeriodoFin()
        );

        // Si no hay gastos, el valor es cero
        if (totalGastado == null) {
            totalGastado = BigDecimal.ZERO;
        }

        // Crear respuesta con cálculos automáticos
        return new PresupuestoEjecucionResponse(presupuesto.getMontoLimite(), totalGastado);
    }

    /**
     * Lista presupuestos activos en una fecha específica.
     * 
     * @param userId ID del usuario autenticado
     * @param fecha Fecha para consultar presupuestos activos
     * @return Lista de presupuestos activos en la fecha
     */
    @Transactional(readOnly = true)
    public List<PresupuestoResponse> listarPresupuestosActivos(UUID userId, LocalDate fecha) {
        List<Presupuesto> presupuestos = presupuestoRepository.findActivosByUsuarioAndFecha(userId, fecha);
        return presupuestos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Convierte entidad Presupuesto a DTO Response.
     * 
     * @param presupuesto Entidad a convertir
     * @return DTO Response
     */
    private PresupuestoResponse mapToResponse(Presupuesto presupuesto) {
        return new PresupuestoResponse(
            presupuesto.getId(),
            presupuesto.getCategoriaId(),
            presupuesto.getMontoLimite(),
            presupuesto.getPeriodoInicio(),
            presupuesto.getPeriodoFin()
        );
    }
}
