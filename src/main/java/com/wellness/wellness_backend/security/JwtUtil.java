package com.wellness.wellness_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(Environment env) {
        String secret = env.getProperty("jwt.secret");
        this.expirationMs = Long.parseLong(
                env.getProperty("jwt.expirationMs", "3600000")
        );
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // =========================
    // TOKEN GENERATION
    // =========================
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // TOKEN PARSING (INTERNAL)
    // =========================
    private Claims claims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // =========================
    // TOKEN READ METHODS
    // =========================
    public String getUsername(String token) {
        return claims(token).getSubject();
    }

    public Long getUserId(String token) {
        return claims(token).get("userId", Long.class);
    }

    public String getRole(String token) {
        return claims(token).get("role", String.class);
    }

    // =========================
    // TOKEN VALIDATION
    // =========================
    public boolean validateToken(String token) {
        try {
            claims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
