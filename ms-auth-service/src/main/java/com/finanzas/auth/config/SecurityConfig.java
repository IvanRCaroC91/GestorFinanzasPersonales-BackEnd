package com.finanzas.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuración de seguridad para el microservicio de autenticación.
 * 
 * Configura Spring Security con CORS habilitado y endpoints públicos
 * para autenticación y health checks.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength de 12 para buena seguridad
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/health", "/health/**").permitAll()
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            // Habilitar CORS con nuestra configuración personalizada
            .cors(cors -> cors.configurationSource(corsConfigurationSource));
        
        return http.build();
    }
}
