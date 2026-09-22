package com.finanzas.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Clase principal del microservicio de autenticación.
// Este servicio maneja el registro de usuarios y la autenticación JWT.
// Se registra en Eureka para que otros microservicios puedan descubrirlo.
@SpringBootApplication
public class AuthServiceApplication {
    
    // Método principal que inicia el servicio de autenticación.
    // Este método es el punto de entrada de la aplicación Spring Boot.
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
