package com.finanzas.finance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración de persistencia para el microservicio financiero.
 * 
 * Habilita JPA repositories y gestión transaccional para operaciones
 * sobre la base de datos PostgreSQL existente.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.finanzas.finance.repository")
@EnableTransactionManagement
public class PersistenceConfig {
    // La configuración principal está en application.yml
    // Esta clase habilita explícitamente los repositorios JPA
}
