package cl.venegas.buses_api.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Proveedor de tokens JWT.
 * Genera, valida y extrae claims de tokens de acceso y refresh tokens.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    // Duración del access token: 15 minutos
    private static final long ACCESS_TOKEN_EXPIRY_MS = 15 * 60 * 1000L;

    // Duración del refresh token: 7 días
    private static final long REFRESH_TOKEN_EXPIRY_MS = 7 * 24 * 60 * 60 * 1000L;

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un access token JWT para el usuario autenticado.
     * Expira en 15 minutos.
     */
    public String generateAccessToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRY_MS);

        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Genera un refresh token JWT.
     * Expira en 7 días. Solo contiene el email (mínimo de información).
     */
    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRY_MS);

        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Valida un token JWT.
     * Retorna true si el token es válido y no ha expirado.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado");
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformado");
        } catch (SecurityException e) {
            log.warn("Firma JWT inválida");
        } catch (IllegalArgumentException e) {
            log.warn("Token JWT vacío o nulo");
        }
        return false;
    }

    /**
     * Extrae el email (subject) de un token JWT.
     */
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrae el rol del token JWT.
     */
    public String getRoleFromToken(String token) {
        return (String) parseClaims(token).get(CLAIM_ROLE);
    }

    /**
     * Verifica si es un access token (no refresh).
     */
    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(parseClaims(token).get(CLAIM_TYPE));
    }

    /**
     * Verifica si es un refresh token.
     */
    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(parseClaims(token).get(CLAIM_TYPE));
    }

    /**
     * Retorna la fecha de expiración del token.
     */
    public Date getExpirationFromToken(String token) {
        return parseClaims(token).getExpiration();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
