package com.finanzas.finance.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.SecretKey;
import java.util.UUID;

/**
 * Interceptor para validar el header X-User-Id en todas las peticiones.
 * 
 * Garantiza que todas las solicitudes a los endpoints de la API
 * incluyan un ID de usuario válido en formato UUID.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0.0
 */
@Component
public class UserValidationInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserValidationInterceptor.class);

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Solo validar para endpoints de la API (excluir actuator, error, etc.)
        if (!path.startsWith("/api/v1/finance/")) {
            return true;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT ausente o inválido");
            return false;
        }

        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            log.warn("Request sin header {}: {} {} - IP: {}", 
                    USER_ID_HEADER, method, path, getClientIpAddress(request));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Identidad de usuario ausente");
            return false;
        }

        try {
            UUID userId = UUID.fromString(userIdHeader.trim());
            Claims claims = parseToken(authorizationHeader.substring(7));
            String tokenUserId = claims.get("userId", String.class);

            if (!userId.toString().equals(tokenUserId)) {
                log.warn("El usuario del token no coincide con {}: {} {} - IP: {}",
                        USER_ID_HEADER, method, path, getClientIpAddress(request));
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Identidad de usuario no coincide con el token");
                return false;
            }

            log.debug("Request validado - Usuario: {} {} {} - IP: {}", 
                    userId, method, path, getClientIpAddress(request));
            return true;
            
        } catch (IllegalArgumentException e) {
            log.warn("Request con {} inválido: {} {} - Valor: {} - IP: {}", 
                    USER_ID_HEADER, method, path, userIdHeader, getClientIpAddress(request));
            
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Identidad de usuario inválida");
            return false;
        } catch (Exception e) {
            log.warn("Request con token JWT inválido: {} {} - IP: {}",
                    method, path, getClientIpAddress(request));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido o expirado");
            return false;
        }
    }

    private Claims parseToken(String token) {
        SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la dirección IP del cliente de forma segura.
     * 
     * @param request HttpServletRequest
     * @return Dirección IP del cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
