package com.finanzas.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración CORS para el API Gateway.
 * 
 * Esta configuración asegura que el gateway maneje correctamente
 * las solicitudes CORS desde el frontend en Vercel y desarrollo local.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Configuration
public class CorsConfig {

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos (producción Vercel específico y desarrollo local)
        List<String> allowedOrigins = Arrays.asList(
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "https://api-gateway-production-f3ef.up.railway.app",
            "https://gestor-finanzas-personales-front-gezyi3jbs.vercel.app",
            "https://*.vercel.app",
            "https://*.railway.app"
        );
        
        // Agregar frontendUrl dinámico si no está ya en la lista
        if (!allowedOrigins.contains(frontendUrl)) {
            allowedOrigins.add(frontendUrl);
        }
        
        configuration.setAllowedOrigins(allowedOrigins);
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers permitidos (todos)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Headers expuestos
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Total-Count"
        ));
        
        // Permitir credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight (1 hora)
        configuration.setMaxAge(3600L);
        
        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return new CorsWebFilter(source);
    }
}
