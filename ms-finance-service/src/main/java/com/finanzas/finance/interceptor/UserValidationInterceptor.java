package com.finanzas.finance.interceptor;

import com.finanzas.finance.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Solo validar para endpoints de la API (excluir actuator, error, etc.)
        if (!path.startsWith("/api/v1/finance/")) {
            return true;
        }

        // Obtener el header X-User-Id
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        
        // Validar que el header esté presente
        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            log.warn("Request sin header {}: {} {} - IP: {}", 
                    USER_ID_HEADER, method, path, getClientIpAddress(request));
            
            throw new ValidationException("El header " + USER_ID_HEADER + " es obligatorio");
        }

        // Validar que sea un UUID válido
        try {
            UUID userId = UUID.fromString(userIdHeader.trim());
            log.debug("Request validado - Usuario: {} {} {} - IP: {}", 
                    userId, method, path, getClientIpAddress(request));
            return true;
            
        } catch (IllegalArgumentException e) {
            log.warn("Request con {} inválido: {} {} - Valor: {} - IP: {}", 
                    USER_ID_HEADER, method, path, userIdHeader, getClientIpAddress(request));
            
            throw new ValidationException("El header " + USER_ID_HEADER + " debe ser un UUID válido");
        }
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
