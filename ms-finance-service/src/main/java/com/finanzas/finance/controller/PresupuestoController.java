package com.finanzas.finance.controller;

import com.finanzas.finance.dto.ApiResponse;
import com.finanzas.finance.dto.PresupuestoRequest;
import com.finanzas.finance.dto.PresupuestoResponse;
import com.finanzas.finance.dto.PresupuestoEjecucionResponse;
import com.finanzas.finance.service.PresupuestoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * manteniendo separación de responsabilidades y delegando
 * toda la lógica de negocio al Service correspondiente.
 * 
 * Base path: /api/v1/finance/presupuestos
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/finance/presupuestos")
public class PresupuestoController {

    private static final Logger log = LoggerFactory.getLogger(PresupuestoController.class);

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
        
        List<PresupuestoResponse> presupuestos = presupuestoService.listarPresupuestosPorUsuario(userId);
        
        ApiResponse<List<PresupuestoResponse>> apiResponse = ApiResponse.success(
            "Presupuestos listados correctamente", presupuestos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Lista presupuestos del usuario filtrados por mes/año.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/presupuestos?mesAnio={mesAnio}
     * 
     * @param mesAnio Mes y año en formato YYYY-MM
     * @param userId ID del usuario autenticado (header)
     * @return Lista de presupuestos filtrados por mes/año
     */
    @GetMapping(params = "periodoInicio")
    public ResponseEntity<ApiResponse<List<PresupuestoResponse>>> listarPresupuestosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos?periodoInicio={} - Usuario: {}", periodoInicio, userId);
        
        List<PresupuestoResponse> presupuestos = presupuestoService.listarPresupuestosPorPeriodo(userId, periodoInicio);
        
        ApiResponse<List<PresupuestoResponse>> apiResponse = ApiResponse.success(
            "Presupuestos filtrados por período correctamente", presupuestos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Busca un presupuesto por ID.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/presupuestos/{id}
     * 
     * @param id ID del presupuesto a buscar
     * @param userId ID del usuario autenticado (header)
     * @return PresupuestoResponse con los datos del presupuesto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PresupuestoResponse>> buscarPresupuesto(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos/{} - Usuario: {}", id, userId);
        
        PresupuestoResponse response = presupuestoService.buscarPresupuestoPorId(id, userId);
        
        ApiResponse<PresupuestoResponse> apiResponse = ApiResponse.success(
            "Presupuesto encontrado correctamente", response);
        
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
     * @return Respuesta con ApiResponse
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPresupuesto(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request DELETE /api/v1/finance/presupuestos/{} - Usuario: {}", id, userId);
        
        presupuestoService.eliminarPresupuesto(id, userId);
        
        ApiResponse<Void> apiResponse = ApiResponse.success("Presupuesto eliminado correctamente");
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Obtiene la ejecución financiera de los presupuestos del usuario para un mes específico.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/presupuestos/ejecucion?mesAnio={mesAnio}
     * 
     * @param mesAnio Mes y año en formato YYYY-MM
     * @param userId ID del usuario autenticado (header)
     * @return Lista con la ejecución de los presupuestos
     */
    @GetMapping("/ejecucion")
    public ResponseEntity<ApiResponse<List<PresupuestoEjecucionResponse>>> obtenerEjecucionPresupuestos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos/ejecucion?periodoInicio={} - Usuario: {}", periodoInicio, userId);
        
        List<PresupuestoEjecucionResponse> ejecuciones = presupuestoService.obtenerEjecucionPresupuestos(userId, periodoInicio);
        
        ApiResponse<List<PresupuestoEjecucionResponse>> apiResponse = ApiResponse.success(
            "Ejecución de presupuestos obtenida correctamente", ejecuciones);
        
        return ResponseEntity.ok(apiResponse);
    }
}
