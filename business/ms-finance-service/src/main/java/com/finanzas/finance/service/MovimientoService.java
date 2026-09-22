package com.finanzas.finance.service;

import com.finanzas.finance.dto.MovimientoRequest;
import com.finanzas.finance.dto.MovimientoResponse;
import com.finanzas.finance.entity.Movimiento;
import com.finanzas.finance.exception.BusinessException;
import com.finanzas.finance.repository.MovimientoRepository;
import com.finanzas.finance.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de movimientos financieros.
 * 
 * Proporciona operaciones CRUD con validaciones de negocio
 * y seguridad por usuario.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Service
@Transactional
public class MovimientoService {

    private static final Logger log = LoggerFactory.getLogger(MovimientoService.class);

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
     * @throws BusinessException si las validaciones fallan
     */
    public MovimientoResponse crearMovimiento(MovimientoRequest request, UUID userId) {
        log.info("Creando movimiento para usuario: {} - Categoría: {} - Valor: {}", 
                userId, request.getCategoriaId(), request.getValor());

        // Validar que la categoría exista y pertenezca al usuario
        var categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        // Validar que el valor sea positivo
        if (request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El valor del movimiento debe ser mayor a cero");
        }

        // Validar consistencia entre tipo de movimiento y tipo de categoría
        if (!categoria.getTipo().name().equals(request.getTipo())) {
            throw new BusinessException("El tipo de movimiento no coincide con el tipo de categoría");
        }

        // Crear entidad
        Movimiento movimiento = new Movimiento();
        movimiento.setCategoriaId(request.getCategoriaId());
        movimiento.setUserId(userId);
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        movimiento.setValor(request.getValor());
        movimiento.setFecha(request.getFecha());
        movimiento.setFacturaId(request.getFacturaId());

        // Guardar
        Movimiento saved = movimientoRepository.save(movimiento);
        
        log.info("Movimiento creado exitosamente - ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    /**
     * Lista todos los movimientos del usuario.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de movimientos del usuario
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorUsuario(UUID userId) {
        log.info("Listando movimientos para usuario: {}", userId);

        List<Movimiento> movimientos = movimientoRepository.findByUserId(userId);
        
        return movimientos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca un movimiento por ID verificando que pertenezca al usuario.
     * 
     * @param id ID del movimiento a buscar
     * @param userId ID del usuario autenticado
     * @return MovimientoResponse con los datos del movimiento
     */
    @Transactional(readOnly = true)
    public MovimientoResponse buscarMovimientoPorId(UUID id, UUID userId) {
        log.info("Buscando movimiento ID: {} para usuario: {}", id, userId);
        
        Movimiento movimiento = movimientoRepository.findById(id)
            .filter(m -> m.getUserId().equals(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));
        
        return mapToResponse(movimiento);
    }

    /**
     * Actualiza un movimiento existente.
     * 
     * @param id ID del movimiento a actualizar
     * @param request Nuevos datos del movimiento
     * @param userId ID del usuario autenticado
     * @return MovimientoResponse actualizado
     */
    public MovimientoResponse actualizarMovimiento(UUID id, MovimientoRequest request, UUID userId) {
        log.info("Actualizando movimiento ID: {} para usuario: {}", id, userId);

        // Validar que el movimiento exista y pertenezca al usuario
        Movimiento existente = movimientoRepository.findById(id)
            .filter(m -> m.getUserId().equals(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));

        // Validar que la categoría exista y pertenezca al usuario
        var categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        // Validar que el valor sea positivo
        if (request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El valor del movimiento debe ser mayor a cero");
        }

        // Validar consistencia entre tipo de movimiento y tipo de categoría
        if (!categoria.getTipo().name().equals(request.getTipo())) {
            throw new BusinessException("El tipo de movimiento no coincide con el tipo de categoría");
        }

        // Actualizar campos
        existente.setCategoriaId(request.getCategoriaId());
        existente.setDescripcion(request.getDescripcion());
        existente.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        existente.setValor(request.getValor());
        existente.setFecha(request.getFecha());
        existente.setFacturaId(request.getFacturaId());

        Movimiento actualizado = movimientoRepository.save(existente);
        
        log.info("Movimiento actualizado exitosamente - ID: {}", actualizado.getId());
        return mapToResponse(actualizado);
    }

    /**
     * Elimina un movimiento existente.
     * 
     * @param id ID del movimiento a eliminar
     * @param userId ID del usuario autenticado
     */
    public void eliminarMovimiento(UUID id, UUID userId) {
        log.info("Eliminando movimiento ID: {} para usuario: {}", id, userId);

        // Validar que el movimiento exista y pertenezca al usuario
        Movimiento existente = movimientoRepository.findById(id)
            .filter(m -> m.getUserId().equals(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));

        movimientoRepository.delete(existente);
        
        log.info("Movimiento eliminado exitosamente - ID: {}", id);
    }

    /**
     * Lista movimientos filtrados por categoría.
     * 
     * @param categoriaId ID de la categoría
     * @param userId ID del usuario autenticado
     * @return Lista de movimientos de la categoría
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorCategoria(UUID categoriaId, UUID userId) {
        log.info("Listando movimientos por categoría: {} - Usuario: {}", categoriaId, userId);

        // Validar que la categoría exista y pertenezca al usuario
        categoriaRepository.findByIdAndUserId(categoriaId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        List<Movimiento> movimientos = movimientoRepository.findByUserIdAndCategoriaId(categoriaId, userId);
        
        return movimientos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte entidad Movimiento a DTO MovimientoResponse.
     */
    private MovimientoResponse mapToResponse(Movimiento movimiento) {
        MovimientoResponse response = new MovimientoResponse();
        response.setId(movimiento.getId());
        response.setCategoriaId(movimiento.getCategoriaId());
        response.setDescripcion(movimiento.getDescripcion());
        response.setTipo(movimiento.getTipo().name());
        response.setValor(movimiento.getValor());
        response.setFecha(movimiento.getFecha());
        response.setCreatedAt(movimiento.getCreatedAt());
        return response;
    }
} 
