package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.service.PractitionerService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/practitioners")
public class PractitionerController {

    private final PractitionerService service;

    public PractitionerController(PractitionerService service) {
        this.service = service;
    }

    @PostMapping
    public Practitioner create(@RequestBody Practitioner p) {
        return service.create(p);
    }

    @GetMapping
    public List<Practitioner> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Practitioner p = service.getById(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePractitioner(
            @PathVariable Long id,
            @RequestBody Practitioner updatedData) {

        Practitioner existing = service.getById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Practitioner not found");
        }

        // update only fields provided
        if (updatedData.getName() != null) existing.setName(updatedData.getName());
        if (updatedData.getSpecialization() != null) existing.setSpecialization(updatedData.getSpecialization());
        if (updatedData.getExperienceYears() != 0) existing.setExperienceYears(updatedData.getExperienceYears());
        if (updatedData.getBio() != null) existing.setBio(updatedData.getBio());
        if (updatedData.getEmail() != null) existing.setEmail(updatedData.getEmail());

        Practitioner saved = service.create(existing); // repo.save()

        return ResponseEntity.ok(saved);
    }

}
