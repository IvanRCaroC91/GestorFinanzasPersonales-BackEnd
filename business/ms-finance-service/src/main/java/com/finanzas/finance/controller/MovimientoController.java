package com.finanzas.finance.controller;

import com.finanzas.finance.dto.ApiResponse;
import com.finanzas.finance.dto.MovimientoRequest;
import com.finanzas.finance.dto.MovimientoResponse;
import com.finanzas.finance.entity.Movimiento;
import com.finanzas.finance.service.MovimientoService;
import com.finanzas.finance.repository.MovimientoRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
@RestController
@RequestMapping("/api/v1/finance/movimientos")
public class MovimientoController {

    private static final Logger log = LoggerFactory.getLogger(MovimientoController.class);

    private final MovimientoService movimientoService;
    private final MovimientoRepository movimientoRepository;

    public MovimientoController(MovimientoService movimientoService, MovimientoRepository movimientoRepository) {
        this.movimientoService = movimientoService;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Crea un nuevo movimiento financiero.
     */
    // Este endpoint permite crear un movimiento financiero.
    // Recibe los datos desde el frontend, los envía al service
    // y guarda la información en la base de datos.
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
     */
    // Este endpoint permite obtener todos los movimientos del usuario.
    // Recibe el ID del usuario desde el header, consulta al service
    // y retorna la lista de movimientos desde la base de datos.
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
     */
    // Este endpoint permite obtener movimientos filtrados por tipo.
    // Recibe el tipo como parámetro, consulta al repository
    // y retorna los movimientos filtrados desde la base de datos.
    @GetMapping(params = "tipo")
    public ResponseEntity<ApiResponse<List<MovimientoResponse>>> listarMovimientosPorTipo(
            @RequestParam String tipo,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request GET /api/v1/finance/movimientos?tipo={} - Usuario: {}", tipo, userId);
        
        List<MovimientoResponse> movimientos = movimientoRepository.findByUserIdAndTipo(
                userId, Movimiento.TipoMovimiento.valueOf(tipo))
                .stream()
                .map(m -> new MovimientoResponse(
                        m.getId(),
                        m.getCategoriaId(),
                        m.getDescripcion(),
                        m.getTipo().name(),
                        m.getValor(),
                        m.getFecha(),
                        m.getCreatedAt()
                ))
                .collect(Collectors.toList());
        
        ApiResponse<List<MovimientoResponse>> apiResponse = ApiResponse.success(
            "Movimientos filtrados por tipo correctamente", movimientos);
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Actualiza un movimiento existente.
     */
    // Este endpoint permite actualizar un movimiento existente.
    // Recibe el ID del movimiento y los nuevos datos, los envía al service
    // y actualiza la información en la base de datos.
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
     */
    // Este endpoint permite eliminar un movimiento existente.
    // Recibe el ID del movimiento, lo envía al service
    // y elimina la información de la base de datos.
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMovimiento(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Request DELETE /api/v1/finance/movimientos/{} - Usuario: {}", id, userId);
        
        movimientoService.eliminarMovimiento(id, userId);
        
        ApiResponse<Void> apiResponse = ApiResponse.success("Movimiento eliminado correctamente");
        
        return ResponseEntity.ok(apiResponse);
    }
}
