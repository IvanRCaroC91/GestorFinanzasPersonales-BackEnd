package com.finanzas.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

// Utilidad para manejar tokens JWT (JSON Web Tokens).
// Esta clase se encarga de generar, validar y extraer información de los tokens.
// Los tokens se usan para mantener la sesión del usuario activa en el sistema.
@Component
public class JwtUtil {

    // Secreto utilizado para firmar los tokens JWT.
    // Debe ser el mismo en todos los microservicios del sistema.
    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    // Tiempo de expiración del token en milisegundos (default: 24 horas).
    // Después de este tiempo, el usuario debe volver a iniciar sesión.
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    // Genera la clave de firma para los tokens JWT usando el secreto configurado.
    // Esta clave se usa para firmar y validar los tokens de forma segura.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un nuevo token JWT para un usuario.
    // El token contiene el username, userId y fecha de expiración.
    // Este token se usa para autenticar las peticiones del usuario.
    public String generateToken(String username, String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)  // Username del usuario
                .claim("userId", userId)  // UUID del usuario
                .issuedAt(now)  // Fecha de creación
                .expiration(expiryDate)  // Fecha de expiración
                .signWith(getSigningKey())  // Firma con la clave secreta
                .compact();
    }

    // Extrae el username del token JWT.
    // El username es el subject del token y sirve como identificador principal.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae la fecha de expiración del token JWT.
    // Se usa para verificar si el token ha expirado.
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Método genérico para extraer cualquier claim del token JWT.
    // Permite obtener información específica del token de forma flexible.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todos los claims (información) del token JWT.
    // Este método es usado internamente por los otros métodos de extracción.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Verifica si el token JWT ha expirado.
    // Compara la fecha de expiración del token con la fecha actual.
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Valida que el token JWT sea correcto para un usuario específico.
    // Verifica que el username coincida y que el token no haya expirado.
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
