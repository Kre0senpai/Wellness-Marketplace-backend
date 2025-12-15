package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.model.PractitionerAvailability;
import com.wellness.wellness_backend.repo.PractitionerAvailabilityRepository;
import com.wellness.wellness_backend.repo.PractitionerRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PractitionerAvailabilityService {

    private final PractitionerAvailabilityRepository repo;
    private final PractitionerRepository practitionerRepo;

    public PractitionerAvailabilityService(
            PractitionerAvailabilityRepository repo,
            PractitionerRepository practitionerRepo) {
        this.repo = repo;
        this.practitionerRepo = practitionerRepo;
    }

    // Practitioner adds availability
    public PractitionerAvailability addSlot(Long userId, LocalDateTime slot) {

        Practitioner practitioner =
                practitionerRepo.findByUserId(userId);

        if (practitioner == null || !practitioner.isVerified()) {
            throw new RuntimeException("Not a verified practitioner");
        }

        repo.findByPractitionerIdAndSlot(practitioner.getId(), slot)
            .ifPresent(a -> {
                throw new RuntimeException("Slot already exists");
            });

        PractitionerAvailability availability =
                new PractitionerAvailability();
        availability.setPractitionerId(practitioner.getId());
        availability.setSlot(slot);

        return repo.save(availability);
    }

    // Public: view available slots
    public List<PractitionerAvailability>
    getAvailableSlots(Long practitionerId) {
        return repo.findByPractitionerIdAndBookedFalse(practitionerId);
    }

    // Booking will call this
    public PractitionerAvailability reserveSlot(
            Long practitionerId, LocalDateTime slot) {

        PractitionerAvailability availability =
                repo.findByPractitionerIdAndSlot(practitionerId, slot)
                        .orElseThrow(() ->
                                new RuntimeException("Slot not available"));

        if (availability.isBooked()) {
            throw new RuntimeException("Slot already booked");
        }

        availability.setBooked(true);
        return repo.save(availability);
    }
}
