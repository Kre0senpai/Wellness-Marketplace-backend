package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.RefreshToken;
import com.wellness.wellness_backend.repo.RefreshTokenRepository;
import com.wellness.wellness_backend.exception.TokenRefreshException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    // TTL for refresh tokens — change if you want (e.g. 7 days)
    private final Duration refreshTokenDuration = Duration.ofDays(7);

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        // Optional: delete old tokens for user when creating a new one
        // repo.deleteByUserId(userId);

        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(refreshTokenDuration));
        token.setRevoked(false);
        return repo.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return repo.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        // 1) Check if revoked
        if (token.isRevoked()) {
            throw new TokenRefreshException("Refresh token was revoked. Please login again.");
        }

        // 2) Check if expired
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(token);
            throw new TokenRefreshException("Refresh token expired. Please login again.");
        }

        return token;
    }


    @Transactional
    public void revokeToken(String token) {
        repo.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            repo.save(t);
        });
    }

    @Transactional
    public void revokeAllForUser(Long userId) {
        // faster route: delete all rows for the user
        repo.deleteByUserId(userId);
    }
}
