package com.finanzas.finance.service;

import com.finanzas.finance.dto.CategoriaRequest;
import com.finanzas.finance.dto.CategoriaResponse;
import com.finanzas.finance.entity.Categoria;
import com.finanzas.finance.exception.ResourceNotFoundException;
import com.finanzas.finance.exception.BusinessException;
import com.finanzas.finance.repository.CategoriaRepository;
import com.finanzas.finance.repository.MovimientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de categorías financieras.
 * 
 * Proporciona operaciones CRUD con validaciones de negocio
 * y seguridad por usuario.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Service
@Transactional
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;
    private final MovimientoRepository movimientoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, 
                           MovimientoRepository movimientoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Crea una nueva categoría para el usuario.
     * 
     * @param request Datos de la categoría a crear
     * @param userId ID del usuario autenticado
     * @return CategoriaResponse con los datos guardados
     * @throws BusinessException si hay violaciones de reglas de negocio
     */
    public CategoriaResponse crearCategoria(CategoriaRequest request, UUID userId) {
        log.info("Creando categoría para usuario: {} - Nombre: {} - Tipo: {}", 
                userId, request.getNombre(), request.getTipo());

        // Validar que no exista una categoría con el mismo nombre para el mismo usuario
        boolean existeNombre = categoriaRepository.existsByUserIdAndNombre(userId, request.getNombre());
        if (existeNombre) {
            throw new BusinessException("Ya existe una categoría con ese nombre para este usuario");
        }

        // Validar que el tipo sea válido
        try {
            Categoria.TipoMovimiento.valueOf(request.getTipo());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de categoría inválido. Debe ser INGRESO o EGRESO");
        }

        // Crear entidad
        Categoria categoria = new Categoria();
        categoria.setUserId(userId);
        categoria.setNombre(request.getNombre());
        categoria.setTipo(Categoria.TipoMovimiento.valueOf(request.getTipo()));
        categoria.setTipoGasto(Categoria.TipoGasto.valueOf(request.getTipoGasto()));
        categoria.setCategoriaPadreId(request.getCategoriaPadreId());

        // Guardar
        Categoria guardada = categoriaRepository.save(categoria);
        
        log.info("Categoría creada exitosamente - ID: {} - Usuario: {}", 
                guardada.getId(), userId);
        
        return mapToResponse(guardada);
    }

    /**
     * Lista todas las categorías del usuario autenticado.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de categorías del usuario
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategoriasPorUsuario(UUID userId) {
        log.info("Listando categorías para usuario: {}", userId);
        
        List<Categoria> categorias = categoriaRepository.findByUserIdOrderByNombreAsc(userId);
        
        List<CategoriaResponse> responses = categorias.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        log.info("Se encontraron {} categorías para usuario: {}", responses.size(), userId);
        return responses;
    }

    /**
     * Lista categorías del usuario filtradas por tipo.
     * 
     * @param userId ID del usuario autenticado
     * @param tipo Tipo de categoría (INGRESO|EGRESO)
     * @return Lista de categorías filtradas por tipo
     * @throws BusinessException si el tipo es inválido
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategoriasPorTipo(UUID userId, String tipo) {
        log.info("Listando categorías por tipo: {} para usuario: {}", tipo, userId);
        
        try {
            Categoria.TipoMovimiento tipoEnum = Categoria.TipoMovimiento.valueOf(tipo);
            List<Categoria> categorias = categoriaRepository.findByUserIdAndTipoOrderByNombreAsc(userId, tipoEnum);
            
            List<CategoriaResponse> responses = categorias.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
                
            log.info("Se encontraron {} categorías de tipo {} para usuario: {}", 
                    responses.size(), tipo, userId);
            return responses;
            
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de categoría inválido. Debe ser INGRESO o EGRESO");
        }
    }

    /**
     * Busca una categoría por ID y usuario.
     * 
     * @param id ID de la categoría
     * @param userId ID del usuario autenticado
     * @return CategoriaResponse con los datos de la categoría
     * @throws ResourceNotFoundException si la categoría no existe o no pertenece al usuario
     */
    @Transactional(readOnly = true)
    public CategoriaResponse buscarCategoriaPorId(UUID id, UUID userId) {
        log.info("Buscando categoría ID: {} para usuario: {}", id, userId);
        
        Categoria categoria = categoriaRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe o no pertenece al usuario"));
        
        return mapToResponse(categoria);
    }

    /**
     * Actualiza una categoría existente.
     * 
     * @param id ID de la categoría a actualizar
     * @param request Nuevos datos de la categoría
     * @param userId ID del usuario autenticado
     * @return CategoriaResponse actualizado
     * @throws ResourceNotFoundException si la categoría no existe
     * @throws BusinessException si hay violaciones de reglas de negocio
     */
    public CategoriaResponse actualizarCategoria(UUID id, CategoriaRequest request, UUID userId) {
        log.info("Actualizando categoría ID: {} para usuario: {}", id, userId);

        // Validar que la categoría exista y pertenezca al usuario
        Categoria existente = categoriaRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe o no pertenece al usuario"));

        // Validar que el nuevo nombre no esté en uso por otra categoría del mismo usuario
        if (!existente.getNombre().equals(request.getNombre())) {
            boolean existeNombre = categoriaRepository.existsByUserIdAndNombre(userId, request.getNombre());
            if (existeNombre) {
                throw new BusinessException("Ya existe otra categoría con ese nombre para este usuario");
            }
        }

        // Validar que el tipo sea válido
        try {
            Categoria.TipoMovimiento.valueOf(request.getTipo());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de categoría inválido. Debe ser INGRESO o EGRESO");
        }

        // Si cambia el tipo, validar que no tenga movimientos asociados
        if (!request.getTipo().equals(existente.getTipo().name())) {
            long countMovimientos = movimientoRepository.countByCategoriaIdAndUserId(id, userId);
            if (countMovimientos > 0) {
                throw new BusinessException(
                    "No se puede cambiar el tipo de categoría porque tiene movimientos asociados");
            }
        }

        // Actualizar campos
        existente.setNombre(request.getNombre());
        existente.setTipo(Categoria.TipoMovimiento.valueOf(request.getTipo()));
        existente.setTipoGasto(Categoria.TipoGasto.valueOf(request.getTipoGasto()));
        existente.setCategoriaPadreId(request.getCategoriaPadreId());

        Categoria actualizada = categoriaRepository.save(existente);
        
        log.info("Categoría actualizada exitosamente - ID: {} - Usuario: {}", 
                actualizada.getId(), userId);
        
        return mapToResponse(actualizada);
    }

    /**
     * Elimina una categoría existente.
     * 
     * @param id ID de la categoría a eliminar
     * @param userId ID del usuario autenticado
     * @throws ResourceNotFoundException si la categoría no existe
     * @throws BusinessException si la categoría tiene movimientos asociados
     */
    public void eliminarCategoria(UUID id, UUID userId) {
        log.info("Eliminando categoría ID: {} para usuario: {}", id, userId);

        // Validar que la categoría exista y pertenezca al usuario
        Categoria existente = categoriaRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe o no pertenece al usuario"));

        // Validar que no tenga movimientos asociados
        long countMovimientos = movimientoRepository.countByCategoriaIdAndUserId(id, userId);
        if (countMovimientos > 0) {
            throw new BusinessException(
                "No se puede eliminar la categoría porque tiene movimientos asociados");
        }

        categoriaRepository.delete(existente);
        
        log.info("Categoría eliminada exitosamente - ID: {} - Usuario: {}", id, userId);
    }

    /**
     * Convierte una entidad Categoria a CategoriaResponse.
     * 
     * @param categoria Entidad a convertir
     * @return DTO con los datos de la categoría
     */
    private CategoriaResponse mapToResponse(Categoria categoria) {
        return new CategoriaResponse(
            categoria.getId(),
            categoria.getNombre(),
            categoria.getTipo().name(),
            categoria.getTipoGasto().name()
        );
    }
}
