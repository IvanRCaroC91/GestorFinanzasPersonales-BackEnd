package com.finanzas.finance.controller;

import com.finanzas.finance.dto.ApiResponse;
import com.finanzas.finance.dto.CategoriaRequest;
import com.finanzas.finance.dto.CategoriaResponse;
import com.finanzas.finance.service.CategoriaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gestión de categorías financieras.
 * 
 * Expone endpoints para operaciones CRUD sobre categorías,
 * manteniendo separación de responsabilidades y delegando
 * toda la lógica de negocio al Service correspondiente.
 * 
 * Base path: /api/v1/finance/categorias
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/finance/categorias")
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * Crea una nueva categoría financiera.
     * 
     * HTTP Method: POST
     * Path: /api/v1/finance/categorias
     * 
     * @param request Datos de la categoría a crear
     * @param userId ID del usuario autenticado (header)
     * @return CategoriaResponse con los datos guardados
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> crearCategoria(
            @Valid @RequestBody CategoriaRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request POST /api/v1/finance/categorias - Usuario: {}", userId);
        
        CategoriaResponse response = categoriaService.crearCategoria(request, userId);
        
        ApiResponse<CategoriaResponse> apiResponse = ApiResponse.success(
            "Categoría creada correctamente", response);
        
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Lista todas las categorías del usuario autenticado.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/categorias
     * 
     * @param userId ID del usuario autenticado (header)
     * @return Lista de categorías del usuario
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listarCategorias(
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/categorias - Usuario: {}", userId);
        
        List<CategoriaResponse> categorias = categoriaService.listarCategoriasPorUsuario(userId);
        
        ApiResponse<List<CategoriaResponse>> apiResponse = ApiResponse.success(
            "Categorías listadas correctamente", categorias);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Lista categorías del usuario filtradas por tipo.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/categorias?tipo={tipo}
     * 
     * @param tipo Tipo de categoría (INGRESO|EGRESO)
     * @param userId ID del usuario autenticado (header)
     * @return Lista de categorías filtradas por tipo
     */
    @GetMapping(params = "tipo")
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listarCategoriasPorTipo(
            @RequestParam String tipo,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/categorias?tipo={} - Usuario: {}", tipo, userId);
        
        List<CategoriaResponse> categorias = categoriaService.listarCategoriasPorTipo(userId, tipo);
        
        ApiResponse<List<CategoriaResponse>> apiResponse = ApiResponse.success(
            "Categorías filtradas por tipo correctamente", categorias);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Busca una categoría por ID.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/categorias/{id}
     * 
     * @param id ID de la categoría a buscar
     * @param userId ID del usuario autenticado (header)
     * @return CategoriaResponse con los datos de la categoría
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> buscarCategoria(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/categorias/{} - Usuario: {}", id, userId);
        
        CategoriaResponse response = categoriaService.buscarCategoriaPorId(id, userId);
        
        ApiResponse<CategoriaResponse> apiResponse = ApiResponse.success(
            "Categoría encontrada correctamente", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Actualiza una categoría existente.
     * 
     * HTTP Method: PUT
     * Path: /api/v1/finance/categorias/{id}
     * 
     * @param id ID de la categoría a actualizar
     * @param request Nuevos datos de la categoría
     * @param userId ID del usuario autenticado (header)
     * @return CategoriaResponse actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizarCategoria(
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request PUT /api/v1/finance/categorias/{} - Usuario: {}", id, userId);
        
        CategoriaResponse response = categoriaService.actualizarCategoria(id, request, userId);
        
        ApiResponse<CategoriaResponse> apiResponse = ApiResponse.success(
            "Categoría actualizada correctamente", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Elimina una categoría existente.
     * 
     * HTTP Method: DELETE
     * Path: /api/v1/finance/categorias/{id}
     * 
     * @param id ID de la categoría a eliminar
     * @param userId ID del usuario autenticado (header)
     * @return Respuesta con ApiResponse
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoria(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request DELETE /api/v1/finance/categorias/{} - Usuario: {}", id, userId);
        
        categoriaService.eliminarCategoria(id, userId);
        
        ApiResponse<Void> apiResponse = ApiResponse.success("Categoría eliminada correctamente");
        
        return ResponseEntity.ok(apiResponse);
    }
}
