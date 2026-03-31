package com.finanzas.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

// Filtro global de autenticación JWT para el API Gateway.
// Este filtro se ejecuta en cada petición que pasa por el gateway
// y valida que el token JWT sea válido antes de permitir el acceso a los microservicios.
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    // Secreto utilizado para firmar y validar los tokens JWT.
    // Se obtiene desde las variables de entorno o usa el valor por defecto.
    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    // Lista de rutas que no requieren autenticación.
    // Estas rutas son públicas para permitir login y registro de usuarios.
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register"
    );

    // Método principal del filtro que se ejecuta en cada petición.
    // Valida el token JWT y agrega información del usuario a los headers.
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Permitir rutas de autenticación sin token
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        // Validar token para las demás rutas
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return handleUnauthorized(exchange);
        }

        String token = authHeader.substring(7);
        
        try {
            if (validateToken(token)) {
                // Extraer userId y username del JWT
                String userId = extractUserId(token);
                String username = extractUsername(token);
                
                // Agregar información del usuario al request para que los microservicios la usen
                return chain.filter(exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Id", userId)           // ← CLAVE: UUID del usuario
                                .header("X-User-Username", username)   // ← Username adicional
                                .build())
                        .build());
            } else {
                return handleUnauthorized(exchange);
            }
        } catch (Exception e) {
            return handleUnauthorized(exchange);
        }
    }

    // Verifica si una ruta está en la lista de rutas excluidas de autenticación.
    // Permite acceso público a login y register.
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(excludedPath -> 
                path.equals(excludedPath) || path.startsWith(excludedPath));
    }

    // Valida que el token JWT sea correcto usando la clave secreta.
    // Retorna true si el token es válido, false si está expirado o es inválido.
    private boolean validateToken(String token) {
        try {
            SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrae el username (subject) del token JWT.
    // El username es el identificador principal del usuario en el sistema.
    private String extractUsername(String token) {
        SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // Extrae el userId del token JWT.
    // El userId es un UUID único que identifica al usuario en la base de datos.
    private String extractUserId(String token) {
        SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("userId", String.class);
    }

    // Maneja las respuestas de error 401 Unauthorized.
    // Retorna un mensaje JSON estándar cuando el token es inválido o ausente.
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = "{\"error\":\"Unauthorized\",\"message\":\"Token JWT inválido o ausente\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    // Define el orden de ejecución del filtro.
    // HIGHEST_PRECEDENCE asegura que este filtro se ejecute primero.
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
