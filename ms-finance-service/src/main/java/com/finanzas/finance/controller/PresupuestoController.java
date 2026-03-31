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
     */
    // Este endpoint permite crear un presupuesto financiero.
    // Recibe los datos desde el frontend, los envía al service
    // y guarda la información en la base de datos.
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
     */
    // Este endpoint permite obtener todos los presupuestos del usuario.
    // Recibe el ID del usuario desde el header, consulta al service
    // y retorna la lista de presupuestos desde la base de datos.
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
     * Lista presupuestos del usuario filtrados por año y mes.
     */
    // Este endpoint permite obtener presupuestos filtrados por período.
    // Recibe el año y mes como parámetros, consulta al service
    // y retorna los presupuestos filtrados desde la base de datos.
    @GetMapping(params = {"anio", "mes"})
    public ResponseEntity<ApiResponse<List<PresupuestoResponse>>> listarPresupuestosPorPeriodo(
            @RequestParam Integer anio,
            @RequestParam Integer mes,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos?anio={}&mes={} - Usuario: {}", anio, mes, userId);
        
        List<PresupuestoResponse> presupuestos = presupuestoService.listarPresupuestosPorPeriodo(userId, anio, mes);
        
        ApiResponse<List<PresupuestoResponse>> apiResponse = ApiResponse.success(
            "Presupuestos filtrados por año y mes correctamente", presupuestos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Busca un presupuesto por ID.
     */
    // Este endpoint permite obtener un presupuesto específico.
    // Recibe el ID del presupuesto, consulta al service
    // y retorna los datos del presupuesto desde la base de datos.
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
     */
    // Este endpoint permite actualizar un presupuesto existente.
    // Recibe el ID del presupuesto y los nuevos datos, los envía al service
    // y actualiza la información en la base de datos.
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
     */
    // Este endpoint permite eliminar un presupuesto existente.
    // Recibe el ID del presupuesto, lo envía al service
    // y elimina la información de la base de datos.
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
     * Obtiene la ejecución financiera de un presupuesto específico.
     */
    // Este endpoint permite obtener la ejecución de un presupuesto.
    // Recibe el ID del presupuesto, consulta al service
    // y retorna las métricas de ejecución desde la base de datos.
    @GetMapping("/{id}/ejecucion")
    public ResponseEntity<ApiResponse<PresupuestoEjecucionResponse>> obtenerEjecucionPresupuesto(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos/{}/ejecucion - Usuario: {}", id, userId);
        
        PresupuestoEjecucionResponse response = presupuestoService.calcularEjecucionPresupuestoIndividual(id, userId);
        
        ApiResponse<PresupuestoEjecucionResponse> apiResponse = ApiResponse.success(
            "Ejecución de presupuesto obtenida correctamente", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Obtiene la ejecución financiera de los presupuestos del usuario para un año y mes específicos.
     */
    // Este endpoint permite obtener la ejecución de presupuestos por período.
    // Recibe el año y mes como parámetros, consulta al service
    // y retorna las métricas de ejecución desde la base de datos.
    @GetMapping("/ejecucion")
    public ResponseEntity<ApiResponse<List<PresupuestoEjecucionResponse>>> obtenerEjecucionPresupuestos(
            @RequestParam Integer anio,
            @RequestParam Integer mes,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/presupuestos/ejecucion?anio={}&mes={} - Usuario: {}", anio, mes, userId);
        
        List<PresupuestoEjecucionResponse> ejecuciones = presupuestoService.obtenerEjecucionPresupuestos(userId, anio, mes);
        
        ApiResponse<List<PresupuestoEjecucionResponse>> apiResponse = ApiResponse.success(
            "Ejecución de presupuestos obtenida correctamente", ejecuciones);
        
        return ResponseEntity.ok(apiResponse);
    }
}
