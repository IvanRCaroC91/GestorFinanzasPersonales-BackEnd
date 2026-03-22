package com.finanzas.finance.service;

import com.finanzas.finance.dto.MovimientoRequest;
import com.finanzas.finance.dto.MovimientoResponse;
import com.finanzas.finance.entity.Movimiento;
import com.finanzas.finance.entity.Categoria;
import com.finanzas.finance.repository.MovimientoRepository;
import com.finanzas.finance.repository.CategoriaRepository;
import com.finanzas.finance.exception.*;
import lombok.extern.slf4j.Slf4j;
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
 * Mejoras implementadas:
 * - Excepciones personalizadas para cada caso de error
 * - Validaciones de seguridad mejoradas
 * - Logging en puntos clave
 * - Métodos desacoplados y reutilizables
 * 
 * @author Sistema de Finanzas Personales
 * @version 2.0.0
 */
@Slf4j
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
     * @throws ResourceNotFoundException si la categoría no existe o no pertenece al usuario
     * @throws BusinessException si el tipo de movimiento no coincide con la categoría
     * @throws ValidationException si los datos son inválidos
     */
    public MovimientoResponse crearMovimiento(MovimientoRequest request, UUID userId) {
        log.info("Creando movimiento para usuario: {}, categoría: {}, valor: {}", 
                userId, request.getCategoriaId(), request.getValor());

        // Validar que la categoría exista y pertenezca al usuario
        Categoria categoria = validarCategoriaPerteneceUsuario(request.getCategoriaId(), userId);

        // Validar que el tipo de movimiento coincida con el tipo de categoría
        validarTipoMovimientoCoincideCategoria(request.getTipo(), categoria);

        // Crear entidad
        Movimiento movimiento = buildMovimientoDesdeRequest(request, userId);

        // Guardar y retornar respuesta
        Movimiento guardado = movimientoRepository.save(movimiento);
        log.info("Movimiento creado exitosamente con ID: {}", guardado.getId());
        
        return mapToResponse(guardado);
    }

    /**
     * Actualiza un movimiento existente con validaciones de seguridad.
     * 
     * @param id ID del movimiento a actualizar
     * @param request Nuevos datos del movimiento
     * @param userId ID del usuario autenticado
     * @return MovimientoResponse actualizado
     * @throws ResourceNotFoundException si el movimiento no existe
     * @throws UnauthorizedException si el movimiento no pertenece al usuario
     * @throws BusinessException si las validaciones de negocio fallan
     */
    public MovimientoResponse actualizarMovimiento(UUID id, MovimientoRequest request, UUID userId) {
        log.info("Actualizando movimiento ID: {} para usuario: {}", id, userId);

        // Buscar movimiento existente validando propiedad del usuario
        Movimiento existente = validarMovimientoPerteneceUsuario(id, userId);

        // Validar categoría si cambia
        if (!request.getCategoriaId().equals(existente.getCategoriaId())) {
            Categoria categoria = validarCategoriaPerteneceUsuario(request.getCategoriaId(), userId);
            validarTipoMovimientoCoincideCategoria(request.getTipo(), categoria);
        }

        // Actualizar campos
        actualizarCamposMovimiento(existente, request);

        Movimiento actualizado = movimientoRepository.save(existente);
        log.info("Movimiento actualizado exitosamente");
        
        return mapToResponse(actualizado);
    }

    /**
     * Elimina un movimiento verificando propiedad del usuario.
     * 
     * @param id ID del movimiento a eliminar
     * @param userId ID del usuario autenticado
     * @throws ResourceNotFoundException si el movimiento no existe
     * @throws UnauthorizedException si el movimiento no pertenece al usuario
     */
    public void eliminarMovimiento(UUID id, UUID userId) {
        log.info("Eliminando movimiento ID: {} para usuario: {}", id, userId);

        Movimiento movimiento = validarMovimientoPerteneceUsuario(id, userId);
        movimientoRepository.delete(movimiento);
        
        log.info("Movimiento eliminado exitosamente");
    }

    /**
     * Lista todos los movimientos de un usuario específico.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de movimientos del usuario
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorUsuario(UUID userId) {
        log.debug("Listando movimientos para usuario: {}", userId);

        List<Movimiento> movimientos = movimientoRepository.findByUserId(userId);
        List<MovimientoResponse> responses = movimientos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        log.debug("Se encontraron {} movimientos para el usuario", responses.size());
        return responses;
    }

    /**
     * Filtra movimientos por tipo para un usuario específico.
     * 
     * @param userId ID del usuario autenticado
     * @param tipo Tipo de movimiento (INGRESO|EGRESO)
     * @return Lista de movimientos filtrados por tipo
     * @throws ValidationException si el tipo de movimiento es inválido
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorTipo(UUID userId, String tipo) {
        log.debug("Listando movimientos para usuario: {}, tipo: {}", userId, tipo);

        try {
            Movimiento.TipoMovimiento tipoEnum = Movimiento.TipoMovimiento.valueOf(tipo);
            List<Movimiento> movimientos = movimientoRepository.findByUserIdAndTipo(userId, tipoEnum);
            
            List<MovimientoResponse> responses = movimientos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

            log.debug("Se encontraron {} movimientos de tipo {} para el usuario", responses.size(), tipo);
            return responses;
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Tipo de movimiento inválido. Debe ser INGRESO o EGRESO");
        }
    }

    // ========================
    // MÉTODOS PRIVADOS DE VALIDACIÓN
    // ========================

    /**
     * Valida que una categoría exista y pertenezca al usuario.
     * 
     * @param categoriaId ID de la categoría a validar
     * @param userId ID del usuario autenticado
     * @return Entidad Categoria validada
     * @throws ResourceNotFoundException si la categoría no existe o no pertenece al usuario
     */
    private Categoria validarCategoriaPerteneceUsuario(UUID categoriaId, UUID userId) {
        return categoriaRepository.findByIdAndUserId(categoriaId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "La categoría no existe o no pertenece al usuario"));
    }

    /**
     * Valida que el tipo de movimiento coincida con el tipo de categoría.
     * 
     * @param tipoMovimiento Tipo de movimiento del request
     * @param categoria Entidad categoría a validar
     * @throws BusinessException si los tipos no coinciden
     */
    private void validarTipoMovimientoCoincideCategoria(String tipoMovimiento, Categoria categoria) {
        if (!tipoMovimiento.equals(categoria.getTipo().name())) {
            throw new BusinessException(
                "El tipo de movimiento no coincide con el tipo de categoría. " +
                "Categoría es de tipo: " + categoria.getTipo().name());
        }
    }

    /**
     * Valida que un movimiento exista y pertenezca al usuario.
     * 
     * @param movimientoId ID del movimiento a validar
     * @param userId ID del usuario autenticado
     * @return Entidad Movimiento validada
     * @throws ResourceNotFoundException si el movimiento no existe
     * @throws UnauthorizedException si el movimiento no pertenece al usuario
     */
    private Movimiento validarMovimientoPerteneceUsuario(UUID movimientoId, UUID userId) {
        return movimientoRepository.findByIdAndUserId(movimientoId, userId)
            .orElseThrow(() -> new UnauthorizedException(
                "El movimiento no existe o no pertenece al usuario"));
    }

    /**
     * Construye una entidad Movimiento desde un DTO Request.
     * 
     * @param request DTO con datos del movimiento
     * @param userId ID del usuario autenticado
     * @return Entidad Movimiento construida
     */
    private Movimiento buildMovimientoDesdeRequest(MovimientoRequest request, UUID userId) {
        Movimiento movimiento = new Movimiento();
        movimiento.setUserId(userId);
        movimiento.setCategoriaId(request.getCategoriaId());
        movimiento.setFacturaId(request.getFacturaId());
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        movimiento.setValor(request.getValor());
        movimiento.setFecha(request.getFecha());
        return movimiento;
    }

    /**
     * Actualiza los campos de una entidad Movimiento desde un DTO Request.
     * 
     * @param movimiento Entidad a actualizar
     * @param request DTO con nuevos datos
     */
    private void actualizarCamposMovimiento(Movimiento movimiento, MovimientoRequest request) {
        movimiento.setCategoriaId(request.getCategoriaId());
        movimiento.setFacturaId(request.getFacturaId());
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setTipo(Movimiento.TipoMovimiento.valueOf(request.getTipo()));
        movimiento.setValor(request.getValor());
        movimiento.setFecha(request.getFecha());
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
