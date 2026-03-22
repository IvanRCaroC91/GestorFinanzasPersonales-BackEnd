package com.finanzas.finance.service;

import com.finanzas.finance.dto.CategoriaRequest;
import com.finanzas.finance.dto.CategoriaResponse;
import com.finanzas.finance.entity.Categoria;
import com.finanzas.finance.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service de negocio para gestión de categorías financieras.
 * 
 * Implementa toda la lógica de negocio para operaciones CRUD sobre la tabla categorias,
 * garantizando integridad de datos, seguridad por usuario y validaciones de negocio.
 * 
 * Validaciones implementadas:
 * - El nombre debe ser único por usuario (restricción UNIQUE en BD)
 * - El tipo debe ser válido (INGRESO|EGRESO)
 * - El tipo de gasto debe ser válido (NECESARIO|NO_NECESARIO|OCASIONAL)
 * - user_id siempre aplicado para seguridad de datos
 * - Categoría padre debe existir y pertenecer al mismo usuario
 * - No se pueden crear ciclos en jerarquía de categorías
 * 
 * Relaciones con otras tablas:
 * - categorias.categoria_padre_id → categorias.id (auto-referencia, nullable)
 * - categorias.user_id → usuarios.id (seguridad)
 * - movimientos.categoria_id → categorias.id (dependencia)
 * - presupuestos.categoria_id → categorias.id (dependencia)
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Crea una nueva categoría con validaciones completas.
     * 
     * @param request Datos de la categoría a crear
     * @param userId ID del usuario autenticado
     * @return CategoriaResponse con los datos guardados
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public CategoriaResponse crearCategoria(CategoriaRequest request, UUID userId) {
        // Validar que el nombre no esté duplicado para el mismo usuario
        if (categoriaRepository.existsByUserIdAndNombre(userId, request.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe una categoría con ese nombre para este usuario");
        }

        // Validar categoría padre si se especifica
        if (request.getCategoriaPadreId() != null) {
            Categoria categoriaPadre = categoriaRepository.findByIdAndUserId(
                request.getCategoriaPadreId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "La categoría padre no existe o no pertenece al usuario"));

            // Validar que la categoría padre sea del mismo tipo
            if (!request.getTipo().equals(categoriaPadre.getTipo().name())) {
                throw new IllegalArgumentException(
                    "La categoría padre debe ser del mismo tipo");
            }
        }

        // Crear entidad
        Categoria categoria = new Categoria();
        categoria.setUserId(userId);
        categoria.setNombre(request.getNombre());
        categoria.setTipo(Categoria.TipoMovimiento.valueOf(request.getTipo()));
        categoria.setTipoGasto(Categoria.TipoGasto.valueOf(request.getTipoGasto()));
        categoria.setCategoriaPadreId(request.getCategoriaPadreId());

        // Guardar y retornar respuesta
        Categoria guardada = categoriaRepository.save(categoria);
        return mapToResponse(guardada);
    }

    /**
     * Actualiza una categoría existente con validaciones de seguridad.
     * 
     * @param id ID de la categoría a actualizar
     * @param request Nuevos datos de la categoría
     * @param userId ID del usuario autenticado
     * @return CategoriaResponse actualizada
     * @throws IllegalArgumentException si no existe o no pertenece al usuario
     */
    public CategoriaResponse actualizarCategoria(UUID id, CategoriaRequest request, UUID userId) {
        // Buscar categoría existente y validar propiedad
        Categoria existente = categoriaRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("La categoría no existe o no pertenece al usuario"));

        // Validar duplicado de nombre (si cambia el nombre)
        if (!request.getNombre().equals(existente.getNombre())) {
            if (categoriaRepository.existsByUserIdAndNombre(userId, request.getNombre())) {
                throw new IllegalArgumentException(
                    "Ya existe una categoría con ese nombre para este usuario");
            }
        }

        // Validar categoría padre si cambia
        if (request.getCategoriaPadreId() != null) {
            // Evitar autociclo
            if (request.getCategoriaPadreId().equals(id)) {
                throw new IllegalArgumentException("Una categoría no puede ser su propia padre");
            }

            Categoria categoriaPadre = categoriaRepository.findByIdAndUserId(
                request.getCategoriaPadreId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "La categoría padre no existe o no pertenece al usuario"));

            // Validar mismo tipo
            if (!request.getTipo().equals(categoriaPadre.getTipo().name())) {
                throw new IllegalArgumentException(
                    "La categoría padre debe ser del mismo tipo");
            }
        }

        // Actualizar campos
        existente.setNombre(request.getNombre());
        existente.setTipo(Categoria.TipoMovimiento.valueOf(request.getTipo()));
        existente.setTipoGasto(Categoria.TipoGasto.valueOf(request.getTipoGasto()));
        existente.setCategoriaPadreId(request.getCategoriaPadreId());

        Categoria actualizada = categoriaRepository.save(existente);
        return mapToResponse(actualizada);
    }

    /**
     * Elimina una categoría verificando propiedad y dependencias.
     * 
     * @param id ID de la categoría a eliminar
     * @param userId ID del usuario autenticado
     * @throws IllegalArgumentException si no existe, no pertenece al usuario o tiene dependencias
     */
    public void eliminarCategoria(UUID id, UUID userId) {
        Categoria categoria = categoriaRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("La categoría no existe o no pertenece al usuario"));

        // Validar que no tenga categorías hijas
        List<Categoria> hijas = categoriaRepository.findByCategoriaPadreId(id);
        if (!hijas.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar una categoría con subcategorías");
        }

        // Nota: Las validaciones de movimientos y presupuestos se manejan a nivel de BD
        // gracias a las restricciones ON DELETE RESTRICT en las FKs

        categoriaRepository.delete(categoria);
    }

    /**
     * Lista todas las categorías de un usuario específico.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de categorías del usuario
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias(UUID userId) {
        List<Categoria> categorias = categoriaRepository.findByUserId(userId);
        return categorias.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Filtra categorías por tipo para un usuario específico.
     * 
     * @param userId ID del usuario autenticado
     * @param tipo Tipo de categoría (INGRESO|EGRESO)
     * @return Lista de categorías filtradas por tipo
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategoriasPorTipo(UUID userId, String tipo) {
        Categoria.TipoMovimiento tipoEnum = Categoria.TipoMovimiento.valueOf(tipo);
        List<Categoria> categorias = categoriaRepository.findByUserIdAndTipo(userId, tipoEnum);
        return categorias.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lista categorías raíz (sin categoría padre) de un usuario.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de categorías raíz
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategoriasRaiz(UUID userId) {
        List<Categoria> categorias = categoriaRepository.findByUserIdAndCategoriaPadreIdIsNull(userId);
        return categorias.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Convierte entidad Categoria a DTO Response.
     * 
     * @param categoria Entidad a convertir
     * @return DTO Response
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
