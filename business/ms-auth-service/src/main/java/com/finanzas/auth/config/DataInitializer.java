package com.finanzas.auth.config;

import com.finanzas.auth.entity.User;
import com.finanzas.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Bean
    @Transactional
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                logger.info("Inicializando usuarios de prueba con BCrypt...");
                
                // Usuario Admin
                User admin = new User();
                admin.setId(UUID.randomUUID());
                admin.setUsername("admin");
                admin.setEmail("admin@finanzas.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setPrimerNombre("Juan");
                admin.setPrimerApellido("Pérez");
                admin.setSegundoNombre("Carlos");
                admin.setCelular("+573011234567");
                admin.setEmailVerificado(false);
                admin.setCelularVerificado(false);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                
                // Usuario User
                User user = new User();
                user.setId(UUID.randomUUID());
                user.setUsername("user");
                user.setEmail("user@finanzas.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setPrimerNombre("María");
                user.setPrimerApellido("García");
                user.setSegundoApellido("López");
                user.setCelular("+573022345678");
                user.setEmailVerificado(false);
                user.setCelularVerificado(false);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                
                userRepository.save(admin);
                userRepository.save(user);
                
                logger.info("Usuarios inicializados correctamente: admin, user");
                logger.info("Hashes BCrypt generados y almacenados en base de datos");
            } else {
                logger.info("Base de datos ya contiene usuarios, omitiendo inicialización");
            }
        };
    }
}
