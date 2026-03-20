package com.finanzas.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global que valida el header Authorization (Bearer JWT) en todas las peticiones.
 *
 * - Permite libre acceso a /api/v1/auth/login
 * - Devuelve 401 cuando el token es inválido o inexistente
 *
 * NOTA: La validación de JWT está simulada y debe ser reemplazada por una verificación real.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String EXEMPT_PATH = "/api/v1/auth/login";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Ruta abierta (no requiere token)
        if (EXEMPT_PATH.equals(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (!isValidBearerToken(authorizationHeader)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isValidBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return false;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        // Validación simulada: reemplazar con validación real de JWT
        return "valid-token".equals(token);
    }

    @Override
    public int getOrder() {
        // Asegura que se ejecute antes de otros filtros configurados.
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
