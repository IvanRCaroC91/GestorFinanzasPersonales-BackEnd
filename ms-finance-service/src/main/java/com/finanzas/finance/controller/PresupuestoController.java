package com.finanzas.finance.controller;

import com.finanzas.finance.dto.ApiResponse;
import com.finanzas.finance.dto.PresupuestoRequest;
import com.finanzas.finance.dto.PresupuestoResponse;
import com.finanzas.finance.dto.PresupuestoEjecucionResponse;
import com.finanzas.finance.service.PresupuestoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gestión de presupuestos financieros.
 * 
 * Expone endpoints para operaciones CRUD sobre presupuestos,
 * incluyendo el endpoint diferencial de ejecución financiera.
 * Mantiene separación de responsabilidades y delega toda la lógica
 * de negocio al Service correspondiente.
 * 
 * Base path: /api/v1/finance/presupuestos
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/finance/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    /**
     * Crea un nuevo presupuesto financiero.
     * 
     * HTTP Method: POST
     * Path: /api/v1/finance/presupuestos
     * 
     * @param request Datos del presupuesto a crear
     * @param userId ID del usuario autenticado (header)
     * @return PresupuestoResponse con los datos guardados
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PresupuestoResponse>> crearPresupuesto(
            @Valid @RequestBody PresupuestoRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request POST /api/v1/finance/presupuestos - Usuario: {}", userId);
        
        PresupuestoResponse response = presupuestoService.crearPresupuesto(request, userId);
        
        ApiResponse<PresupuestoResponse> apiResponse = ApiResponse.success(
            "Presupuesto creado correctamente", response);
        
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Lista todos los presupuestos del usuario autenticado.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/presupuestos
     * 
     * @param userId ID del usuario autenticado (header)
     * @return Lista de presupuestos del usuario
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PresupuestoResponse>>> listarPresupuestos(
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos - Usuario: {}", userId);
        
        List<PresupuestoResponse> presupuestos = presupuestoService.listarPresupuestos(userId);
        
        ApiResponse<List<PresupuestoResponse>> apiResponse = ApiResponse.success(
            "Presupuestos listados correctamente", presupuestos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Lista presupuestos activos en una fecha específica.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/presupuestos?fecha={fecha}
     * 
     * @param fecha Fecha para consultar presupuestos activos (yyyy-MM-dd)
     * @param userId ID del usuario autenticado (header)
     * @return Lista de presupuestos activos en la fecha
     */
    @GetMapping(params = "fecha")
    public ResponseEntity<ApiResponse<List<PresupuestoResponse>>> listarPresupuestosActivos(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos?fecha={} - Usuario: {}", fecha, userId);
        
        List<PresupuestoResponse> presupuestos = presupuestoService.listarPresupuestosActivos(userId, fecha);
        
        ApiResponse<List<PresupuestoResponse>> apiResponse = ApiResponse.success(
            "Presupuestos activos listados correctamente", presupuestos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Obtiene un presupuesto específico por ID.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/presupuestos/{id}
     * 
     * @param id ID del presupuesto a buscar
     * @param userId ID del usuario autenticado (header)
     * @return PresupuestoResponse encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PresupuestoResponse>> obtenerPresupuesto(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos/{} - Usuario: {}", id, userId);
        
        PresupuestoResponse response = presupuestoService.obtenerPresupuestoPorId(id, userId);
        
        ApiResponse<PresupuestoResponse> apiResponse = ApiResponse.success(
            "Presupuesto obtenido correctamente", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 🔥 ENDPOINT DIFERENCIAL - Calcula la ejecución financiera de un presupuesto.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/presupuestos/{id}/ejecucion
     * 
     * Este método es CRÍTICO y calcula métricas clave del rendimiento del presupuesto:
     * - totalGastado: Suma de egresos en el período
     * - disponible: montoLimite - totalGastado
     * - porcentajeUsado: (totalGastado / montoLimite) * 100
     * 
     * @param id ID del presupuesto a evaluar
     * @param userId ID del usuario autenticado (header)
     * @return PresupuestoEjecucionResponse con métricas de ejecución
     */
    @GetMapping("/{id}/ejecucion")
    public ResponseEntity<ApiResponse<PresupuestoEjecucionResponse>> calcularEjecucionPresupuesto(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos/{}/ejecucion - Usuario: {}", id, userId);
        
        PresupuestoEjecucionResponse response = presupuestoService.calcularEjecucionPresupuesto(id, userId);
        
        ApiResponse<PresupuestoEjecucionResponse> apiResponse = ApiResponse.success(
            "Ejecución financiera calculada correctamente", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Actualiza un presupuesto existente.
     * 
     * HTTP Method: PUT
     * Path: /api/v1/finance/presupuestos/{id}
     * 
     * @param id ID del presupuesto a actualizar
     * @param request Nuevos datos del presupuesto
     * @param userId ID del usuario autenticado (header)
     * @return PresupuestoResponse actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PresupuestoResponse>> actualizarPresupuesto(
            @PathVariable UUID id,
            @Valid @RequestBody PresupuestoRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request PUT /api/v1/finance/presupuestos/{} - Usuario: {}", id, userId);
        
        PresupuestoResponse response = presupuestoService.actualizarPresupuesto(id, request, userId);
        
        ApiResponse<PresupuestoResponse> apiResponse = ApiResponse.success(
            "Presupuesto actualizado correctamente", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Elimina un presupuesto existente.
     * 
     * HTTP Method: DELETE
     * Path: /api/v1/finance/presupuestos/{id}
     * 
     * @param id ID del presupuesto a eliminar
     * @param userId ID del usuario autenticado (header)
     * @return Respuesta vacía con código 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPresupuesto(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request DELETE /api/v1/finance/presupuestos/{} - Usuario: {}", id, userId);
        
        presupuestoService.eliminarPresupuesto(id, userId);
        
        ApiResponse<Void> apiResponse = ApiResponse.success("Presupuesto eliminado correctamente");
        
        return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
    }
}
