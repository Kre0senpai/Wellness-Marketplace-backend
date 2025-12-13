package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.model.RefreshToken;
import com.wellness.wellness_backend.repo.UserRepository;
import com.wellness.wellness_backend.service.RefreshTokenService;
import com.wellness.wellness_backend.security.JwtUtil;
import com.wellness.wellness_backend.exception.TokenRefreshException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/api/users/auth")  // ⭐ THIS MAKES THE URL: /api/users/auth/login
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email").toLowerCase().trim();
        String password = body.get("password");

        User u = userRepository.findByEmail(email).orElse(null);
        if (u == null || !passwordEncoder.matches(password, u.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error","Invalid credentials"));
        }

        String accessToken = jwtUtil.generateToken(null, u.getEmail(), u.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(u.getId());

        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken.getToken(),
            "role", u.getRole(),
            "userId", u.getId()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String,String> body) {
        String requestToken = body.get("refreshToken");
        if (requestToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Refresh token is required"));
        }

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new TokenRefreshException("User not found for refresh token"));

        String newAccess = jwtUtil.generateToken(null, user.getEmail(), user.getRole());

        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String,String> body) {
        String requestToken = body.get("refreshToken");
        if (requestToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Refresh token is required"));
        }
        refreshTokenService.revokeToken(requestToken);
        return ResponseEntity.ok(Map.of("message","Logged out"));
    }
}
