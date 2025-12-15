package com.wellness.wellness_backend.repo;

import com.wellness.wellness_backend.model.PractitionerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PractitionerAvailabilityRepository
        extends JpaRepository<PractitionerAvailability, Long> {

    List<PractitionerAvailability> findByPractitionerIdAndBookedFalse(Long practitionerId);

    Optional<PractitionerAvailability>
    findByPractitionerIdAndSlot(Long practitionerId, LocalDateTime slot);
}
