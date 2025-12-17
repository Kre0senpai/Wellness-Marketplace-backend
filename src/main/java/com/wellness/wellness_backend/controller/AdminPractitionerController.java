package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.service.PractitionerService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/practitioners")
public class AdminPractitionerController {

    private final PractitionerService practitionerService;

    public AdminPractitionerController(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }
    
    // ================================
    // VERIFY PRACTITIONER (ADMIN ONLY)
    // ================================
    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyPractitioner(@PathVariable Long id) {

        Practitioner practitioner = practitionerService.getById(id);

        if (practitioner == null) {
            return ResponseEntity.notFound().build();
        }

        // 🔒 CRITICAL BUSINESS CHECK
        if (practitioner.getCertificatePath() == null ||
            practitioner.getCertificatePath().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Cannot verify practitioner without certificate upload");
        }

        practitioner.setVerified(true);
        practitionerService.updatePractitioner(practitioner);

        return ResponseEntity.ok("Practitioner verified successfully");
    }
    // ================================
    // REJECT PRACTITIONER (ADMIN ONLY)
    // ================================
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectPractitioner(@PathVariable Long id) {

        Practitioner practitioner = practitionerService.getById(id);

        if (practitioner == null) {
            return ResponseEntity.notFound().build();
        }

        practitionerService.deletePractitioner(id);

        return ResponseEntity.ok("Practitioner rejected and removed");
    }
}
