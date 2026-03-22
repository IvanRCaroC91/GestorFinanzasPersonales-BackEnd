package com.finanzas.finance.controller;

import com.finanzas.finance.dto.ApiResponse;
import com.finanzas.finance.dto.MovimientoRequest;
import com.finanzas.finance.dto.MovimientoResponse;
import com.finanzas.finance.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gestión de movimientos financieros.
 * 
 * Expone endpoints para operaciones CRUD sobre movimientos,
 * manteniendo separación de responsabilidades y delegando
 * toda la lógica de negocio al Service correspondiente.
 * 
 * Base path: /api/v1/finance/movimientos
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/finance/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    /**
     * Crea un nuevo movimiento financiero.
     * 
     * HTTP Method: POST
     * Path: /api/v1/finance/movimientos
     * 
     * @param request Datos del movimiento a crear
     * @param userId ID del usuario autenticado (header)
     * @return MovimientoResponse con los datos guardados
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoResponse>> crearMovimiento(
            @Valid @RequestBody MovimientoRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request POST /api/v1/finance/movimientos - Usuario: {}", userId);
        
        MovimientoResponse response = movimientoService.crearMovimiento(request, userId);
        
        ApiResponse<MovimientoResponse> apiResponse = ApiResponse.success(
            "Movimiento creado correctamente", response);
        
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Lista todos los movimientos del usuario autenticado.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/movimientos
     * 
     * @param userId ID del usuario autenticado (header)
     * @return Lista de movimientos del usuario
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoResponse>>> listarMovimientos(
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/movimientos - Usuario: {}", userId);
        
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorUsuario(userId);
        
        ApiResponse<List<MovimientoResponse>> apiResponse = ApiResponse.success(
            "Movimientos listados correctamente", movimientos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Lista movimientos del usuario filtrados por tipo.
     * 
     * HTTP Method: GET
     * Path: /api/v1/finance/movimientos?tipo={tipo}
     * 
     * @param tipo Tipo de movimiento (INGRESO|EGRESO)
     * @param userId ID del usuario autenticado (header)
     * @return Lista de movimientos filtrados por tipo
     */
    @GetMapping(params = "tipo")
    public ResponseEntity<ApiResponse<List<MovimientoResponse>>> listarMovimientosPorTipo(
            @RequestParam String tipo,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/movimientos?tipo={} - Usuario: {}", tipo, userId);
        
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorTipo(userId, tipo);
        
        ApiResponse<List<MovimientoResponse>> apiResponse = ApiResponse.success(
            "Movimientos filtrados por tipo correctamente", movimientos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Actualiza un movimiento existente.
     * 
     * HTTP Method: PUT
     * Path: /api/v1/finance/movimientos/{id}
     * 
     * @param id ID del movimiento a actualizar
     * @param request Nuevos datos del movimiento
     * @param userId ID del usuario autenticado (header)
     * @return MovimientoResponse actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoResponse>> actualizarMovimiento(
            @PathVariable UUID id,
            @Valid @RequestBody MovimientoRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request PUT /api/v1/finance/movimientos/{} - Usuario: {}", id, userId);
        
        MovimientoResponse response = movimientoService.actualizarMovimiento(id, request, userId);
        
        ApiResponse<MovimientoResponse> apiResponse = ApiResponse.success(
            "Movimiento actualizado correctamente", response);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Elimina un movimiento existente.
     * 
     * HTTP Method: DELETE
     * Path: /api/v1/finance/movimientos/{id}
     * 
     * @param id ID del movimiento a eliminar
     * @param userId ID del usuario autenticado (header)
     * @return Respuesta vacía con código 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMovimiento(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request DELETE /api/v1/finance/movimientos/{} - Usuario: {}", id, userId);
        
        movimientoService.eliminarMovimiento(id, userId);
        
        ApiResponse<Void> apiResponse = ApiResponse.success("Movimiento eliminado correctamente");
        
        return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
    }
}
