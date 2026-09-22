package com.finanzas.finance.config;

import com.finanzas.finance.interceptor.UserValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración MVC para el microservicio de finanzas.
 * 
 * Registra interceptores y configuraciones adicionales
 * para el manejo de peticiones web.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserValidationInterceptor userValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userValidationInterceptor)
                .addPathPatterns("/api/v1/finance/**")
                .excludePathPatterns(
                    "/api/v1/finance/actuator/**",
                    "/api/v1/finance/health/**",
                    "/api/v1/finance/info/**"
                );
    }
}
