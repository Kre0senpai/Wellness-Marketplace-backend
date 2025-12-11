package com.wellness.wellness_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(org.springframework.core.env.Environment env) {
        String secret = env.getProperty("jwt.secret");
        this.expirationMs = Long.parseLong(env.getProperty("jwt.expirationMs", "3600000"));
        // create key from secret bytes (must be sufficiently long)
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return validateToken(token).getBody().getSubject();
    }

    public String getRole(String token) {
        Object r = validateToken(token).getBody().get("role");
        return r == null ? null : r.toString();
    }
}
