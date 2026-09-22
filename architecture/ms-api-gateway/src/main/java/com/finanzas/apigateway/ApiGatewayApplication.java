package com.finanzas.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Punto de entrada del microservicio API Gateway.
 * Se registra como cliente Eureka y enruta peticiones hacia los demás microservicios.
 */
@SpringBootApplication
@EnableDiscoveryClient  // Habilita el cliente para descubrir otros microservicios en Eureka
public class ApiGatewayApplication {

    // Método principal que inicia el API Gateway.
    // Este servicio actúa como puerta de entrada única para todas las peticiones del frontend.
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
