package com.finanzas.finance.service;

import com.finanzas.finance.dto.MovimientoRequest;
import com.finanzas.finance.dto.MovimientoResponse;
import com.finanzas.finance.entity.Movimiento;
import com.finanzas.finance.entity.Categoria;
import com.finanzas.finance.repository.MovimientoRepository;
import com.finanzas.finance.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service de negocio para gestión de movimientos financieros.
 * 
 * Implementa toda la lógica de negocio para operaciones CRUD sobre la tabla movimientos,
 * garantizando integridad de datos, seguridad por usuario y validaciones de negocio.
 * 
 * Validaciones implementadas:
 * - La categoría debe existir y pertenecer al usuario
 * - El valor debe ser mayor a cero (validado en BD y aquí)
 * - El tipo debe ser válido (INGRESO|EGRESO)
 * - user_id siempre aplicado para seguridad de datos
 * - factura_id opcional pero debe existir si se proporciona
 * 
 * Relaciones con otras tablas:
 * - movimientos.categoria_id → categorias.id (obligatorio)
 * - movimientos.factura_id → facturas.id (opcional)
 * - movimientos.user_id → usuarios.id (seguridad)
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Service
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CategoriaRepository categoriaRepository;

    public MovimientoService(MovimientoRepository movimientoRepository, 
                           CategoriaRepository categoriaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Crea un nuevo movimiento financiero con validaciones completas.
     * 
     * @param request Datos del movimiento a crear
     * @param userId ID del usuario autenticado
     * @return MovimientoResponse con los datos guardados
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public MovimientoResponse crearMovimiento(MovimientoRequest request, UUID userId) {
        // Validar que la categoría exista y pertenezca al usuario
        Categoria categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
            .orElseThrow(() -> new IllegalArgumentException(
                "La categoría no existe o no pertenece al usuario"));

        // Validar que el tipo de movimiento coincida con el tipo de categoría
        if (!request.getTipo().equals(categoria.getTipo().name())) {
            throw new IllegalArgumentException(
                "El tipo de movimiento no coincide con el tipo de categoría");
        }

        // Crear entidad
        Movimiento movimiento = new Movimiento();
        movimiento.setUserId(userId);
        movimiento.setCategoriaId(request.getCategoriaId());
        movimiento.setFacturaId(request.getFacturaId());
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        movimiento.setValor(request.getValor());
        movimiento.setFecha(request.getFecha());

        // Guardar y retornar respuesta
        Movimiento guardado = movimientoRepository.save(movimiento);
        return mapToResponse(guardado);
    }

    /**
     * Actualiza un movimiento existente con validaciones de seguridad.
     * 
     * @param id ID del movimiento a actualizar
     * @param request Nuevos datos del movimiento
     * @param userId ID del usuario autenticado
     * @return MovimientoResponse actualizado
     * @throws IllegalArgumentException si no existe o no pertenece al usuario
     */
    public MovimientoResponse actualizarMovimiento(UUID id, MovimientoRequest request, UUID userId) {
        // Buscar movimiento existente y validar propiedad
        Movimiento existente = movimientoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El movimiento no existe"));

        if (!existente.getUserId().equals(userId)) {
            throw new IllegalArgumentException("El movimiento no pertenece al usuario");
        }

        // Validar categoría si cambia
        if (!request.getCategoriaId().equals(existente.getCategoriaId())) {
            Categoria categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "La categoría no existe o no pertenece al usuario"));

            if (!request.getTipo().equals(categoria.getTipo().name())) {
                throw new IllegalArgumentException(
                    "El tipo de movimiento no coincide con el tipo de categoría");
            }
        }

        // Actualizar campos
        existente.setCategoriaId(request.getCategoriaId());
        existente.setFacturaId(request.getFacturaId());
        existente.setDescripcion(request.getDescripcion());
        existente.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        existente.setValor(request.getValor());
        existente.setFecha(request.getFecha());

        Movimiento actualizado = movimientoRepository.save(existente);
        return mapToResponse(actualizado);
    }

    /**
     * Elimina un movimiento verificando propiedad del usuario.
     * 
     * @param id ID del movimiento a eliminar
     * @param userId ID del usuario autenticado
     * @throws IllegalArgumentException si no existe o no pertenece al usuario
     */
    public void eliminarMovimiento(UUID id, UUID userId) {
        Movimiento movimiento = movimientoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El movimiento no existe"));

        if (!movimiento.getUserId().equals(userId)) {
            throw new IllegalArgumentException("El movimiento no pertenece al usuario");
        }

        movimientoRepository.delete(movimiento);
    }

    /**
     * Lista todos los movimientos de un usuario específico.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de movimientos del usuario
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorUsuario(UUID userId) {
        List<Movimiento> movimientos = movimientoRepository.findByUserId(userId);
        return movimientos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Filtra movimientos por tipo para un usuario específico.
     * 
     * @param userId ID del usuario autenticado
     * @param tipo Tipo de movimiento (INGRESO|EGRESO)
     * @return Lista de movimientos filtrados por tipo
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorTipo(UUID userId, String tipo) {
        Movimiento.TipoMovimiento tipoEnum = Movimiento.TipoMovimiento.valueOf(tipo);
        List<Movimiento> movimientos = movimientoRepository.findByUserIdAndTipo(userId, tipoEnum);
        return movimientos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Convierte entidad Movimiento a DTO Response.
     * 
     * @param movimiento Entidad a convertir
     * @return DTO Response
     */
    private MovimientoResponse mapToResponse(Movimiento movimiento) {
        return new MovimientoResponse(
            movimiento.getId(),
            movimiento.getCategoriaId(),
            movimiento.getDescripcion(),
            movimiento.getTipo().name(),
            movimiento.getValor(),
            movimiento.getFecha(),
            movimiento.getCreatedAt()
        );
    }
}
