package com.finanzas.auth.service;

import com.finanzas.auth.dto.LoginRequest;
import com.finanzas.auth.dto.LoginResponse;
import com.finanzas.auth.entity.User;
import com.finanzas.auth.repository.UserRepository;
import com.finanzas.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Validar entrada
            if (loginRequest == null || 
                loginRequest.getUsername() == null || 
                loginRequest.getPassword() == null ||
                loginRequest.getUsername().trim().isEmpty() || 
                loginRequest.getPassword().trim().isEmpty()) {
                
                logger.warn("Intento de login con credenciales vacías");
                return new LoginResponse("Credenciales inválidas", false, null, null);
            }

            String username = loginRequest.getUsername().trim();
            String password = loginRequest.getPassword().trim();

            // Buscar usuario en la base de datos
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.warn("Usuario no encontrado: {}", username);
                return new LoginResponse("Credenciales inválidas", false, null, null);
            }

            User user = userOptional.get();
            
            // Validar contraseña con BCrypt
            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("Contraseña incorrecta para usuario: {}", username);
                return new LoginResponse("Credenciales inválidas", false, null, null);
            }

            // Generar token JWT
            String token = jwtUtil.generateToken(username);
            
            // Construir respuesta con datos del usuario
            LoginResponse response = new LoginResponse(
                "Login exitoso", 
                true, 
                user.getUsername(),
                user.getEmail(),
                user.getNombreCompleto(),
                user.getIniciales(),
                token
            );

            logger.info("Login exitoso para usuario: {} a las: {}", username, LocalDateTime.now());
            return response;

        } catch (Exception e) {
            logger.error("Error durante el proceso de login para usuario: {}", 
                       loginRequest != null ? loginRequest.getUsername() : "null", e);
            return new LoginResponse("Error interno del servidor", false, null, null);
        }
    }

    /**
     * Encriptar contraseña con BCrypt
     */
    public String encryptPassword(String plainPassword) {
        try {
            if (plainPassword == null || plainPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
            }
            return passwordEncoder.encode(plainPassword.trim());
        } catch (Exception e) {
            logger.error("Error al encriptar contraseña", e);
            throw new RuntimeException("Error al procesar la contraseña");
        }
    }

    /**
     * Validar si una contraseña coincide con el hash almacenado
     */
    public boolean validatePassword(String plainPassword, String hashedPassword) {
        try {
            if (plainPassword == null || hashedPassword == null) {
                return false;
            }
            return passwordEncoder.matches(plainPassword.trim(), hashedPassword);
        } catch (Exception e) {
            logger.error("Error al validar contraseña", e);
            return false;
        }
    }
}
