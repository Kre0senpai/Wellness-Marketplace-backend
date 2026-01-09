package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.dto.PractitionerDTO;
import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.service.PractitionerService;
import com.wellness.wellness_backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/practitioners")
public class PractitionerController {

    private final PractitionerService service;
    private final UserService userService;

    public PractitionerController(PractitionerService service,
                                  UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    // ================================
    // CREATE PRACTITIONER (USER ONLY)
    // ================================
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PractitionerDTO dto,
            Principal principal) {

        User user = userService.getByEmail(principal.getName());
        Practitioner created = service.createPractitioner(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ================================
    // UPLOAD CERTIFICATE (OWNER)
    // ================================
    @PreAuthorize("hasRole('USER') or hasRole('PRACTITIONER')")
    @PostMapping("/certificate")
    public ResponseEntity<?> uploadCertificate(
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        User user = userService.getByEmail(principal.getName());
        service.uploadCertificate(user.getId(), file);

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
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
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
            Principal principal) {

        Practitioner existing = service.getById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Practitioner not found");
        }

        User user = userService.getByEmail(principal.getName());

        if (!user.getId().equals(existing.getUserId())) {
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
}
