package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.dto.PractitionerDTO;
import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.security.AuthUser;
import com.wellness.wellness_backend.service.PractitionerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users/practitioners")
public class PractitionerController {

    private final PractitionerService service;

    public PractitionerController(PractitionerService service) {
        this.service = service;
    }

    // ================================
    // CREATE PRACTITIONER (USER ONLY)
    // ================================
    @PreAuthorize("hasRole('USER') or hasRole('PRACTITIONER')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PractitionerDTO dto,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        Practitioner created = service.createPractitioner(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ================================
    // UPLOAD CERTIFICATE (OWNER)
    // ================================
    @PreAuthorize("hasRole('USER') or hasRole('PRACTITIONER')")
    @PostMapping("/certificate")
    public ResponseEntity<?> uploadCertificate(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam("certificate") MultipartFile certificate
    ) {

        Long userId = authUser.getUserId();
        service.uploadCertificate(userId, certificate);

        return ResponseEntity.ok("Certificate uploaded successfully");
    }

    // ================================
    // GET VERIFIED PRACTITIONERS (PUBLIC)
    // ================================
    @GetMapping
    public ResponseEntity<List<Practitioner>> getPractitioners(
            @RequestParam(required = false) String specialization) {

        return ResponseEntity.ok(
                service.getVerifiedPractitioners(specialization)
        );
    }

    // ================================
    // GET PRACTITIONER BY ID (PUBLIC)
    // ================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Practitioner p = service.getById(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    // ================================
    // UPDATE PRACTITIONER (OWNER)
    // ================================
    @PreAuthorize("hasRole('USER') or hasRole('PRACTITIONER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PractitionerDTO dto,
            Authentication authentication) {

        Practitioner existing = service.getById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Practitioner not found");
        }

        Long userId = extractUserId(authentication);

        if (!userId.equals(existing.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot update this practitioner");
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getSpecialization() != null) existing.setSpecialization(dto.getSpecialization());
        if (dto.getBio() != null) existing.setBio(dto.getBio());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getExperienceYears() != null)
            existing.setExperienceYears(dto.getExperienceYears());

        Practitioner saved = service.updatePractitioner(existing);
        return ResponseEntity.ok(saved);
    }

    // ================================
    // EXTRACT USER ID FROM JWT
    // ================================
    private Long extractUserId(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthUser authUser) {
            return authUser.getUserId();
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid authentication principal"
        );
    }
}
