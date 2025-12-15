package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.PractitionerAvailability;
import com.wellness.wellness_backend.security.AuthUser;
import com.wellness.wellness_backend.service.PractitionerAvailabilityService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class PractitionerAvailabilityController {

    private final PractitionerAvailabilityService service;

    public PractitionerAvailabilityController(
            PractitionerAvailabilityService service) {
        this.service = service;
    }

    // Practitioner adds slot
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> addSlot(
            @RequestParam LocalDateTime slot,
            Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        return ResponseEntity.ok(
                service.addSlot(user.getUserId(), slot)
        );
    }

    // Public: view slots
    @GetMapping("/{practitionerId}")
    public ResponseEntity<List<PractitionerAvailability>>
    getSlots(@PathVariable Long practitionerId) {
        return ResponseEntity.ok(
                service.getAvailableSlots(practitionerId)
        );
    }
}
