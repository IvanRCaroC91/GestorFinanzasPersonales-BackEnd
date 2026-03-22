package com.finanzas.auth.service;

import com.finanzas.auth.dto.LoginRequest;
import com.finanzas.auth.dto.LoginResponse;
import com.finanzas.auth.dto.RegisterRequest;
import com.finanzas.auth.dto.RegisterResponse;
import com.finanzas.auth.entity.User;
import com.finanzas.auth.repository.UserRepository;
import com.finanzas.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Este método implementa el flujo completo de registro de usuarios,
     * incluyendo validaciones de unicidad, encriptación de contraseña
     * y persistencia en base de datos.
     * 
     * @param registerRequest DTO con los datos del usuario a registrar
     * @return RegisterResponse con el resultado del proceso de registro
     * @throws IllegalArgumentException si los datos son inválidos o el usuario ya existe
     */
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        try {
            // Paso 1: Validar que el request no sea nulo
            if (registerRequest == null) {
                logger.warn("Intento de registro con request nulo");
                return RegisterResponse.error("Los datos de registro son obligatorios");
            }

            // Paso 2: Extraer y limpiar datos del request
            String username = registerRequest.getUsername().trim();
            String email = registerRequest.getEmail().trim().toLowerCase();
            String password = registerRequest.getPassword().trim();
            String celular = registerRequest.getCelular().trim();

            // Paso 3: Validar unicidad del usuario en la base de datos
            logger.info("Iniciando validación de unicidad para usuario: {}", username);
            
            if (userRepository.existsByUsername(username)) {
                logger.warn("Intento de registro con username ya existente: {}", username);
                return RegisterResponse.error("El nombre de usuario ya está en uso");
            }

            if (userRepository.existsByEmail(email)) {
                logger.warn("Intento de registro con email ya existente: {}", email);
                return RegisterResponse.error("El correo electrónico ya está registrado");
            }

            if (userRepository.existsByCelular(celular)) {
                logger.warn("Intento de registro con celular ya existente: {}", celular);
                return RegisterResponse.error("El número de celular ya está registrado");
            }

            // Paso 4: Encriptar la contraseña usando BCrypt
            logger.info("Encriptando contraseña para usuario: {}", username);
            String encryptedPassword = encryptPassword(password);

            // Paso 5: Crear la entidad User con los datos validados
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(encryptedPassword);
            newUser.setPrimerNombre(registerRequest.getPrimerNombre().trim());
            newUser.setPrimerApellido(registerRequest.getPrimerApellido().trim());
            
            // Campos opcionales (pueden ser nulos)
            if (registerRequest.getSegundoNombre() != null && !registerRequest.getSegundoNombre().trim().isEmpty()) {
                newUser.setSegundoNombre(registerRequest.getSegundoNombre().trim());
            }
            
            if (registerRequest.getSegundoApellido() != null && !registerRequest.getSegundoApellido().trim().isEmpty()) {
                newUser.setSegundoApellido(registerRequest.getSegundoApellido().trim());
            }
            
            newUser.setCelular(celular);
            newUser.setEmailVerificado(false); // Por defecto no verificado
            newUser.setCelularVerificado(false); // Por defecto no verificado

            // Paso 6: Persistir el usuario en la base de datos
            logger.info("Persistiendo nuevo usuario en base de datos: {}", username);
            User savedUser = userRepository.save(newUser);

            // Paso 7: Construir respuesta de éxito
            logger.info("Usuario registrado exitosamente: {} a las: {}", username, LocalDateTime.now());
            return RegisterResponse.success(
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getPrimerNombre(),
                savedUser.getPrimerApellido(),
                savedUser.getSegundoNombre(),
                savedUser.getSegundoApellido()
            );

        } catch (DataIntegrityViolationException e) {
            // Capturar violaciones de restricciones de base de datos
            logger.error("Error de integridad de datos al registrar usuario", e);
            return RegisterResponse.error("Error: El usuario ya existe en el sistema");
            
        } catch (Exception e) {
            // Capturar cualquier otro error inesperado
            logger.error("Error inesperado durante el registro de usuario", e);
            return RegisterResponse.error("Error interno del servidor durante el registro");
        }
    }

    /**
     * Autentica un usuario en el sistema.
     * 
     * @param loginRequest DTO con credenciales de login
     * @return LoginResponse con resultado del proceso de autenticación
     */
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
