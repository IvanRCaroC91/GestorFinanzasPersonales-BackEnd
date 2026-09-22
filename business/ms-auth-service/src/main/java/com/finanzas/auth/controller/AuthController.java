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

// Controlador REST que expone endpoints de autenticación.
// Maneja el login y registro de usuarios en el sistema.
// Recibe peticiones del frontend y delega la lógica al servicio de autenticación.
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Servicio que contiene la lógica de negocio de autenticación.
    // Se inyecta automáticamente por Spring.
    @Autowired
    private AuthService authService;

    /**
     * Endpoint para autenticar usuarios en el sistema.
     */
    // Este endpoint permite autenticar usuarios en el sistema.
    // Recibe las credenciales desde el frontend, las envía al service
    // y retorna un token JWT si las credenciales son válidas.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Delega la validación de credenciales al servicio de autenticación.
        LoginResponse response = authService.login(loginRequest);
        
        // Si el login es exitoso, retorna 200 OK con el token JWT.
        if (response.isSuccess()) {
            logger.info("Login exitoso para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } else {
            // Si las credenciales son inválidas, retorna 400 Bad Request.
            logger.warn("Login fallido para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para registrar nuevos usuarios en el sistema.
     */
    // Este endpoint permite registrar nuevos usuarios en el sistema.
    // Recibe los datos desde el frontend, los envía al service
    // y crea la cuenta del usuario en la base de datos.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Recibida solicitud de registro para usuario: {}", 
                       registerRequest != null ? registerRequest.getUsername() : "null");
            
            // Delega el registro de usuario al servicio de autenticación.
            RegisterResponse response = authService.registerUser(registerRequest);
            
            // Si el registro es exitoso, retorna 201 Created con los datos del usuario.
            if (response.isSuccess()) {
                logger.info("Registro exitoso para usuario: {}", response.getUsername());
                // HTTP 201 Created es más apropiado para recursos creados exitosamente
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                // Si hay errores de validación o datos duplicados, retorna 400 Bad Request.
                logger.warn("Registro fallido: {}", response.getMessage());
                // HTTP 400 Bad Request para errores de validación o datos duplicados
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            // Maneja errores inesperados durante el registro.
            logger.error("Error inesperado en endpoint de registro", e);
            RegisterResponse errorResponse = RegisterResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
