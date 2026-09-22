package com.finanzas.finance.service;

import com.finanzas.finance.dto.PresupuestoRequest;
import com.finanzas.finance.dto.PresupuestoResponse;
import com.finanzas.finance.dto.PresupuestoEjecucionResponse;
import com.finanzas.finance.entity.Presupuesto;
import com.finanzas.finance.entity.Categoria;
import com.finanzas.finance.exception.BusinessException;
import com.finanzas.finance.repository.PresupuestoRepository;
import com.finanzas.finance.repository.CategoriaRepository;
import com.finanzas.finance.repository.MovimientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de presupuestos financieros.
 * 
 * Proporciona operaciones CRUD con validaciones de negocio
 * y seguridad por usuario.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Service
@Transactional
public class PresupuestoService {

    private static final Logger log = LoggerFactory.getLogger(PresupuestoService.class);

    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoRepository movimientoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository,
                            CategoriaRepository categoriaRepository,
                            MovimientoRepository movimientoRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.categoriaRepository = categoriaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Crea un nuevo presupuesto para el usuario.
     * 
     * @param request Datos del presupuesto a crear
     * @param userId ID del usuario autenticado
     * @return PresupuestoResponse con los datos guardados
     * @throws BusinessException si hay violaciones de reglas de negocio
     * @throws ResourceNotFoundException si la categoría no existe
     */
    public PresupuestoResponse crearPresupuesto(PresupuestoRequest request, UUID userId) {
        log.info("Creando presupuesto para usuario: {} - Categoría: {} - Año: {} - Mes: {}", 
                userId, request.getCategoriaId(), request.getAnio(), request.getMes());

        // Validar que la categoría exista y pertenezca al usuario
        Categoria categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoría no existe o no pertenece al usuario"));

        // Validar que la categoría sea de tipo EGRESO
        if (!categoria.getTipo().equals(Categoria.TipoMovimiento.EGRESO)) {
            throw new BusinessException("Solo se pueden crear presupuestos para categorías de egresos");
        }

        // Validar que no exista un presupuesto para la misma categoría, año y mes
        boolean existePresupuesto = presupuestoRepository.existsByUserIdAndCategoriaIdAndAnioAndMes(
            userId, request.getCategoriaId(), request.getAnio(), request.getMes());
        if (existePresupuesto) {
            throw new BusinessException("Ya existe un presupuesto para esta categoría en el mismo período");
        }

        // Validar que el monto sea positivo
        if (request.getMontoLimite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto límite debe ser mayor a cero");
        }

        // Crear entidad
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setUserId(userId);
        presupuesto.setCategoriaId(request.getCategoriaId());
        presupuesto.setMontoLimite(request.getMontoLimite());
        presupuesto.setAnio(request.getAnio());
        presupuesto.setMes(request.getMes());

        // Guardar
        Presupuesto guardado = presupuestoRepository.save(presupuesto);
        
        log.info("Presupuesto creado exitosamente - ID: {} - Usuario: {}", 
                guardado.getId(), userId);
        
        return mapToResponse(guardado);
    }

    /**
     * Lista todos los presupuestos del usuario autenticado.
     * 
     * @param userId ID del usuario autenticado
     * @return Lista de presupuestos del usuario
     */
    @Transactional(readOnly = true)
    public List<PresupuestoResponse> listarPresupuestosPorUsuario(UUID userId) {
        log.info("Listando presupuestos para usuario: {}", userId);
        
        List<Presupuesto> presupuestos = presupuestoRepository.findByUserIdOrderByAnioDescMesDescCategoriaIdAsc(userId);
        
        List<PresupuestoResponse> responses = presupuestos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        log.info("Se encontraron {} presupuestos para usuario: {}", responses.size(), userId);
        return responses;
    }

    /**
     * Lista presupuestos del usuario filtrados por año y mes.
     * 
     * @param userId ID del usuario autenticado
     * @param anio Año del presupuesto
     * @param mes Mes del presupuesto
     * @return Lista de presupuestos filtrados por año y mes
     */
    @Transactional(readOnly = true)
    public List<PresupuestoResponse> listarPresupuestosPorPeriodo(UUID userId, Integer anio, Integer mes) {
        log.info("Listando presupuestos para usuario: {} - Año: {} - Mes: {}", userId, anio, mes);
        
        List<Presupuesto> presupuestos = presupuestoRepository.findByUserIdAndAnioAndMesOrderByCategoriaIdAsc(userId, anio, mes);
        
        List<PresupuestoResponse> responses = presupuestos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        log.info("Se encontraron {} presupuestos para usuario: {} - Año: {} - Mes: {}", responses.size(), userId, anio, mes);
        return responses;
    }

    /**
     * Busca un presupuesto por ID y usuario.
     * 
     * @param id ID del presupuesto
     * @param userId ID del usuario autenticado
     * @return PresupuestoResponse con los datos del presupuesto
     * @throws ResourceNotFoundException si el presupuesto no existe o no pertenece al usuario
     */
    @Transactional(readOnly = true)
    public PresupuestoResponse buscarPresupuestoPorId(UUID id, UUID userId) {
        log.info("Buscando presupuesto ID: {} para usuario: {}", id, userId);
        
        Presupuesto presupuesto = presupuestoRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El presupuesto no existe o no pertenece al usuario"));
        
        return mapToResponse(presupuesto);
    }

    /**
     * Actualiza un presupuesto existente.
     * 
     * @param id ID del presupuesto a actualizar
     * @param request Nuevos datos del presupuesto
     * @param userId ID del usuario autenticado
     * @return PresupuestoResponse actualizado
     * @throws ResourceNotFoundException si el presupuesto no existe
     * @throws BusinessException si hay violaciones de reglas de negocio
     */
    public PresupuestoResponse actualizarPresupuesto(UUID id, PresupuestoRequest request, UUID userId) {
        log.info("Actualizando presupuesto ID: {} para usuario: {}", id, userId);

        // Validar que el presupuesto exista y pertenezca al usuario
        Presupuesto existente = presupuestoRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El presupuesto no existe o no pertenece al usuario"));

        // Si cambia la categoría, validar que exista y pertenezca al usuario
        if (!request.getCategoriaId().equals(existente.getCategoriaId())) {
            Categoria categoria = categoriaRepository.findByIdAndUserId(request.getCategoriaId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoría no existe o no pertenece al usuario"));

            if (!categoria.getTipo().equals(Categoria.TipoMovimiento.EGRESO)) {
                throw new BusinessException("Solo se pueden asignar presupuestos a categorías de egresos");
            }
        }

        // Si cambia el año o mes, validar que no exista otro presupuesto para la misma categoría, año y mes
        if (!request.getAnio().equals(existente.getAnio()) || 
            !request.getMes().equals(existente.getMes()) ||
            !request.getCategoriaId().equals(existente.getCategoriaId())) {
            
            boolean existePresupuesto = presupuestoRepository.existsByUserIdAndCategoriaIdAndAnioAndMesAndIdNot(
                userId, request.getCategoriaId(), request.getAnio(), request.getMes(), id);
            if (existePresupuesto) {
                throw new BusinessException("Ya existe un presupuesto para esta categoría en el mismo período");
            }
        }

        // Validar que el monto sea positivo
        if (request.getMontoLimite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto límite debe ser mayor a cero");
        }

        // Actualizar campos
        existente.setCategoriaId(request.getCategoriaId());
        existente.setMontoLimite(request.getMontoLimite());
        existente.setAnio(request.getAnio());
        existente.setMes(request.getMes());

        Presupuesto actualizado = presupuestoRepository.save(existente);
        
        log.info("Presupuesto actualizado exitosamente - ID: {} - Usuario: {}", 
                actualizado.getId(), userId);
        
        return mapToResponse(actualizado);
    }

    /**
     * Elimina un presupuesto existente.
     * 
     * @param id ID del presupuesto a eliminar
     * @param userId ID del usuario autenticado
     * @throws ResourceNotFoundException si el presupuesto no existe
     */
    public void eliminarPresupuesto(UUID id, UUID userId) {
        log.info("Eliminando presupuesto ID: {} para usuario: {}", id, userId);

        // Validar que el presupuesto exista y pertenezca al usuario
        Presupuesto existente = presupuestoRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El presupuesto no existe o no pertenece al usuario"));

        presupuestoRepository.delete(existente);
        
        log.info("Presupuesto eliminado exitosamente - ID: {} - Usuario: {}", id, userId);
    }

    /**
     * Obtiene la ejecución financiera de los presupuestos de un usuario para un año y mes específicos.
     * 
     * @param userId ID del usuario autenticado
     * @param anio Año del presupuesto
     * @param mes Mes del presupuesto
     * @return Lista con la ejecución de los presupuestos
     */
    @Transactional(readOnly = true)
    public List<PresupuestoEjecucionResponse> obtenerEjecucionPresupuestos(UUID userId, Integer anio, Integer mes) {
        log.info("Obteniendo ejecución de presupuestos - Usuario: {} - Año: {} - Mes: {}", userId, anio, mes);

        // Obtener todos los presupuestos del usuario para el año y mes
        List<Presupuesto> presupuestos = presupuestoRepository.findByUserIdAndAnioAndMesOrderByCategoriaIdAsc(userId, anio, mes);

        return presupuestos.stream()
            .map(presupuesto -> {
                // Calcular el monto gastado en la categoría durante el año y mes
                BigDecimal montoGastado = movimientoRepository.sumValorByCategoriaIdAndUserIdAndAnioAndMes(
                    userId, presupuesto.getCategoriaId(), presupuesto.getAnio(), presupuesto.getMes());
                
                if (montoGastado == null) {
                    montoGastado = BigDecimal.ZERO;
                }

                // Calcular porcentaje de ejecución
                BigDecimal porcentajeEjecucion = BigDecimal.ZERO;
                if (presupuesto.getMontoLimite().compareTo(BigDecimal.ZERO) > 0) {
                    porcentajeEjecucion = montoGastado
                        .multiply(BigDecimal.valueOf(100))
                        .divide(presupuesto.getMontoLimite(), 2, RoundingMode.HALF_UP);
                }

                // Calcular monto disponible
                BigDecimal montoDisponible = presupuesto.getMontoLimite().subtract(montoGastado);

                // Determinar estado
                String estado = "DENTRO_PRESUPUESTO";
                if (montoGastado.compareTo(presupuesto.getMontoLimite()) > 0) {
                    estado = "EXCEDIDO";
                } else if (montoGastado.compareTo(presupuesto.getMontoLimite().multiply(BigDecimal.valueOf(0.8))) > 0) {
                    estado = "CERCA_LIMITE";
                }

                return new PresupuestoEjecucionResponse(
                    presupuesto.getId(),
                    presupuesto.getCategoriaId(),
                    presupuesto.getMontoLimite(),
                    montoGastado,
                    montoDisponible,
                    porcentajeEjecucion,
                    presupuesto.getAnio(),
                    presupuesto.getMes()
                );
            })
            .collect(Collectors.toList());
    }

    /**
     * Calcula la ejecución financiera de un presupuesto específico.
     * 
     * @param id ID del presupuesto a consultar
     * @param userId ID del usuario autenticado
     * @return PresupuestoEjecucionResponse con métricas de ejecución
     */
    @Transactional(readOnly = true)
    public PresupuestoEjecucionResponse calcularEjecucionPresupuestoIndividual(UUID id, UUID userId) {
        log.info("Calculando ejecución de presupuesto individual - ID: {} - Usuario: {}", id, userId);

        // Obtener el presupuesto específico
        Presupuesto presupuesto = presupuestoRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto no encontrado con ID: " + id));

        // Calcular el monto gastado en la categoría durante el año y mes
        BigDecimal montoGastado = movimientoRepository.sumValorByCategoriaIdAndUserIdAndAnioAndMes(
            userId, presupuesto.getCategoriaId(), presupuesto.getAnio(), presupuesto.getMes());

        if (montoGastado == null) {
            montoGastado = BigDecimal.ZERO;
        }

        // Calcular porcentaje de ejecución
        BigDecimal porcentajeEjecucion = BigDecimal.ZERO;
        if (presupuesto.getMontoLimite().compareTo(BigDecimal.ZERO) > 0) {
            porcentajeEjecucion = montoGastado
                .multiply(BigDecimal.valueOf(100))
                .divide(presupuesto.getMontoLimite(), 2, RoundingMode.HALF_UP);
        }

        // Calcular disponible
        BigDecimal disponible = presupuesto.getMontoLimite().subtract(montoGastado);

        return new PresupuestoEjecucionResponse(
            presupuesto.getId(),
            presupuesto.getCategoriaId(),
            presupuesto.getMontoLimite(),
            montoGastado,
            disponible,
            porcentajeEjecucion,
            presupuesto.getAnio(),
            presupuesto.getMes()
        );
    }

    /**
     * Convierte una entidad Presupuesto a PresupuestoResponse.
     * 
     * @param presupuesto Entidad a convertir
     * @return DTO con los datos del presupuesto
     */
    private PresupuestoResponse mapToResponse(Presupuesto presupuesto) {
        return new PresupuestoResponse(
            presupuesto.getId(),
            presupuesto.getCategoriaId(),
            presupuesto.getMontoLimite(),
            presupuesto.getAnio(),
            presupuesto.getMes()
        );
    }
}
