package com.wellness.wellness_backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;

import com.wellness.wellness_backend.security.JwtUtil;
import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.service.UserService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService service;
	private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserController(UserService service, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

	@GetMapping
	public List<User> getAllUsers() {
		return service.getAllUsers();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<?> getOne(@PathVariable Long id) {
		User user = service.getById(id);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		return ResponseEntity.ok(user);
	}
	
	// inside UserController class

	// GET current logged-in user's profile
	@GetMapping("/me")
	public ResponseEntity<?> me(Authentication authentication) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
	    }

	    String email = authentication.getName(); // email as set by JwtAuthenticationFilter
	    var user = service.getByEmail(email);
	    if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    // avoid returning password hash
	    user.setPassword(null);
	    return ResponseEntity.ok(user);
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody User user) {
		try {
			User saved = service.register(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(saved);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User loginRequest) {
		User existing = service.getByEmail(loginRequest.getEmail());
		if (existing == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
		}

		// use PasswordEncoder to compare supplied password with stored (hashed)
		// password
		if (!passwordEncoder.matches(loginRequest.getPassword(), existing.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
		}

		// generate JWT token
		String token = jwtUtil.generateToken(existing.getEmail(), existing.getRole());

		// return structured JSON with token
		return ResponseEntity.ok(java.util.Map.of("status", "success", "userId", existing.getId(), "role",
				existing.getRole(), "token", token));
	}

}
