package com.finanzas.finance.service;

import com.finanzas.finance.dto.MovimientoRequest;
import com.finanzas.finance.dto.MovimientoResponse;
import com.finanzas.finance.entity.Movimiento;
import com.finanzas.finance.exception.ResourceNotFoundException;
import com.finanzas.finance.exception.BusinessException;
import com.finanzas.finance.repository.MovimientoRepository;
import com.finanzas.finance.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
     * @throws ResourceNotFoundException si la categoría no existe
     */
    public MovimientoResponse crearMovimiento(MovimientoRequest request, UUID userId) {
        log.info("Creando movimiento para usuario: {} - Tipo: {} - Valor: {}", 
                userId, request.getTipo(), request.getValor());

        // Validar que la categoría exista y pertenezca al usuario
        var categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "La categoría no existe o no pertenece al usuario"));

        // Validar que el tipo de movimiento coincida con el tipo de categoría
        if (!request.getTipo().equals(categoria.getTipo().name())) {
            throw new BusinessException(
                "El tipo de movimiento no coincide con el tipo de categoría");
        }

        // Validar que el valor sea positivo para ingresos y negativo para egresos
        if ("INGRESO".equals(request.getTipo()) && request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El valor de un ingreso debe ser positivo");
        } else if ("EGRESO".equals(request.getTipo()) && request.getValor().compareTo(BigDecimal.ZERO) >= 0) {
            throw new BusinessException("El valor de un egreso debe ser negativo");
        }

        // Crear entidad
        Movimiento movimiento = new Movimiento();
        movimiento.setUserId(userId);
        movimiento.setCategoriaId(request.getCategoriaId());
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        movimiento.setValor(request.getValor());
        movimiento.setFecha(request.getFecha());
        movimiento.setFacturaId(request.getFacturaId());

        // Guardar
        Movimiento guardado = movimientoRepository.save(movimiento);
        
        log.info("Movimiento creado exitosamente - ID: {} - Usuario: {}", 
                guardado.getId(), userId);
        
        return mapToResponse(guardado);
    }

    /**
     * Lista todos los movimientos del usuario autenticado.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de movimientos del usuario
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorUsuario(UUID userId) {
        log.info("Listando movimientos para usuario: {}", userId);
        
        List<Movimiento> movimientos = movimientoRepository.findByUserIdOrderByFechaDesc(userId);
        
        List<MovimientoResponse> responses = movimientos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        log.info("Se encontraron {} movimientos para usuario: {}", responses.size(), userId);
        return responses;
    }

    /**
     * Lista movimientos del usuario filtrados por tipo.
     * 
     * @param userId ID del usuario autenticado
     * @param tipo Tipo de movimiento (INGRESO|EGRESO)
     * @return Lista de movimientos filtrados por tipo
     * @throws BusinessException si el tipo es inválido
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorTipo(UUID userId, String tipo) {
        log.info("Listando movimientos por tipo: {} para usuario: {}", tipo, userId);
        
        try {
            Movimiento.TipoMovimiento tipoEnum = Movimiento.TipoMovimiento.valueOf(tipo);
            List<Movimiento> movimientos = movimientoRepository.findByUserIdAndTipoOrderByFechaDesc(userId, tipoEnum);
            
            List<MovimientoResponse> responses = movimientos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
                
            log.info("Se encontraron {} movimientos de tipo {} para usuario: {}", 
                    responses.size(), tipo, userId);
            return responses;
            
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de movimiento inválido. Debe ser INGRESO o EGRESO");
        }
    }

    /**
     * Actualiza un movimiento existente.
     * 
     * @param id ID del movimiento a actualizar
     * @param request Nuevos datos del movimiento
     * @param userId ID del usuario autenticado
     * @return MovimientoResponse actualizado
     * @throws ResourceNotFoundException si el movimiento no existe
     * @throws BusinessException si hay violaciones de reglas de negocio
     */
    public MovimientoResponse actualizarMovimiento(UUID id, MovimientoRequest request, UUID userId) {
        log.info("Actualizando movimiento ID: {} para usuario: {}", id, userId);

        // Validar que el movimiento exista y pertenezca al usuario
        Movimiento existente = movimientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("El movimiento no existe"));

        if (!existente.getUserId().equals(userId)) {
            throw new BusinessException("El movimiento no pertenece al usuario");
        }

        // Validar categoría si cambia
        if (!request.getCategoriaId().equals(existente.getCategoriaId())) {
            var categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "La categoría no existe o no pertenece al usuario"));

            if (!request.getTipo().equals(categoria.getTipo().name())) {
                throw new BusinessException(
                    "El tipo de movimiento no coincide con el tipo de categoría");
            }
        }

        // Validar valor según tipo
        if ("INGRESO".equals(request.getTipo()) && request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El valor de un ingreso debe ser positivo");
        } else if ("EGRESO".equals(request.getTipo()) && request.getValor().compareTo(BigDecimal.ZERO) >= 0) {
            throw new BusinessException("El valor de un egreso debe ser negativo");
        }

        // Actualizar campos
        existente.setCategoriaId(request.getCategoriaId());
        existente.setDescripcion(request.getDescripcion());
        existente.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        existente.setValor(request.getValor());
        existente.setFecha(request.getFecha());
        existente.setFacturaId(request.getFacturaId());

        Movimiento actualizado = movimientoRepository.save(existente);
        
        log.info("Movimiento actualizado exitosamente - ID: {} - Usuario: {}", 
                actualizado.getId(), userId);
        
        return mapToResponse(actualizado);
    }

    /**
     * Elimina un movimiento existente.
     * 
     * @param id ID del movimiento a eliminar
     * @param userId ID del usuario autenticado
     * @throws ResourceNotFoundException si el movimiento no existe
     * @throws BusinessException si el movimiento no pertenece al usuario
     */
    public void eliminarMovimiento(UUID id, UUID userId) {
        log.info("Eliminando movimiento ID: {} para usuario: {}", id, userId);

        // Validar que el movimiento exista y pertenezca al usuario
        Movimiento existente = movimientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("El movimiento no existe"));

        if (!existente.getUserId().equals(userId)) {
            throw new BusinessException("El movimiento no pertenece al usuario");
        }

        movimientoRepository.delete(existente);
        
        log.info("Movimiento eliminado exitosamente - ID: {} - Usuario: {}", id, userId);
    }

    /**
     * Convierte una entidad Movimiento a MovimientoResponse.
     * 
     * @param movimiento Entidad a convertir
     * @return DTO con los datos del movimiento
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
