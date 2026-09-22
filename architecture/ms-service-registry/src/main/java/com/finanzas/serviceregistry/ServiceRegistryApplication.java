package com.finanzas.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// Clase principal del microservicio Service Registry (Eureka Server).
// Este servicio actúa como un directorio central donde todos los demás microservicios
// se registran para poder encontrarse entre sí en una arquitectura de microservicios.
// Es el primer servicio que debe iniciarse en el sistema.
@SpringBootApplication
@EnableEurekaServer  // Habilita el servidor Eureka para descubrimiento de servicios
public class ServiceRegistryApplication {
    
    // Metodo principal que inicia el servidor Eureka.
    // Este metodo es el punto de entrada de la aplicación Spring Boot.
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
