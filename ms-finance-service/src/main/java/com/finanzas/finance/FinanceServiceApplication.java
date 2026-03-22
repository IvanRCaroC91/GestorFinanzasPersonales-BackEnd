package com.finanzas.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Clase principal del microservicio de gestión financiera.
 * 
 * Este microservicio gestiona las operaciones financieras personales incluyendo:
 * - Movimientos (ingresos y egresos)
 * - Categorías de clasificación
 * - Presupuestos por categoría y período
 * - Reportes de ejecución financiera
 * 
 * Se registra en Eureka Service Registry para descubrimiento de servicios.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FinanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceServiceApplication.class, args);
    }
}
