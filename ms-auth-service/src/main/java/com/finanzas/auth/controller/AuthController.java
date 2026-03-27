package com.finanzas.auth.controller;

import com.finanzas.auth.dto.LoginRequest;
import com.finanzas.auth.dto.LoginResponse;
import com.finanzas.auth.dto.RegisterRequest;
import com.finanzas.auth.dto.RegisterResponse;
import com.finanzas.auth.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para autenticar usuarios en el sistema.
     * 
     * @param loginRequest DTO con credenciales de login validadas
     * @return ResponseEntity con LoginResponse y código HTTP apropiado
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        
        if (response.isSuccess()) {
            logger.info("Login exitoso para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Login fallido para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para registrar nuevos usuarios en el sistema.
     * 
     * Este endpoint permite el registro público de nuevos usuarios
     * sin requerir autenticación previa. Realiza validaciones
     * completas de los datos y retorna información del usuario registrado.
     * 
     * @param registerRequest DTO con datos del usuario a registrar, validados con Bean Validation
     * @return ResponseEntity con RegisterResponse y código HTTP apropiado
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Recibida solicitud de registro para usuario: {}", 
                       registerRequest != null ? registerRequest.getUsername() : "null");
            
            RegisterResponse response = authService.registerUser(registerRequest);
            
            if (response.isSuccess()) {
                logger.info("Registro exitoso para usuario: {}", response.getUsername());
                // HTTP 201 Created es más apropiado para recursos creados exitosamente
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                logger.warn("Registro fallido: {}", response.getMessage());
                // HTTP 400 Bad Request para errores de validación o datos duplicados
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error inesperado en endpoint de registro", e);
            RegisterResponse errorResponse = RegisterResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
