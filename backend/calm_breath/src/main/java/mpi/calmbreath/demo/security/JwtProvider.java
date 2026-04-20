package mpi.calmbreath.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Провайдер для работы с JWT токенами
 */
@Component
@Slf4j
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;
    
    /**
     * Генерация Access Token
     */
    public String generateAccessToken(String email, UUID userId, List<? extends GrantedAuthority> authorities) {
        return generateToken(
                email,
                userId.toString(),
                authorities,
                jwtExpirationMs,
                "access"
        );
    }
    
    /**
     * Генерация Refresh Token
     */
    public String generateRefreshToken(String email, UUID userId) {
        return generateToken(
                email,
                userId.toString(),
                null,
                refreshTokenExpirationMs,
                "refresh"
        );
    }
    
    /**
     * Генерация JWT токена
     */
    private String generateToken(
            String email,
            String userId,
            List<? extends GrantedAuthority> authorities,
            long expirationTime,
            String tokenType) {
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tokenType", tokenType);
        
        if (authorities != null) {
            claims.put("roles", authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList());
        }
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Извлечение email из токена
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    /**
     * Извлечение userId из токена
     */
    public String getUserIdFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId");
    }
    
    /**
     * Валидация JWT токена
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
    
    /**
     * Получить время жизни access token в миллисекундах
     */
    public long getAccessTokenExpiration() {
        return jwtExpirationMs;
    }
}
