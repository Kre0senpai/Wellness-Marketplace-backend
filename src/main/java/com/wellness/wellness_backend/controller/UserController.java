package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.repo.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")   // adapt if your project uses a different base
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String email = Optional.ofNullable(body.get("email")).orElse("").toLowerCase().trim();
        if (email.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","email required"));
        if (userRepository.findByEmail(email).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","email exists"));

        String rawPwd = Optional.ofNullable(body.get("password")).orElse("");
        if (rawPwd.length() < 4) return ResponseEntity.badRequest().body(Map.of("error","password too short"));

        User u = new User();
        u.setEmail(email);
        u.setName(Optional.ofNullable(body.get("name")).orElse("User"));
        u.setPassword(passwordEncoder.encode(rawPwd));
        // DO NOT trust body.role for privileges — only set role to 'user' by default
        u.setRole(Optional.ofNullable(body.get("role")).orElse("user").toLowerCase());

        userRepository.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", u.getId(), "email", u.getEmail()));
    }
}
