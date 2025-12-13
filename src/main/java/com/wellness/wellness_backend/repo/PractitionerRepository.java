package com.wellness.wellness_backend.repo;

import com.wellness.wellness_backend.model.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PractitionerRepository extends JpaRepository<Practitioner, Long> {
    boolean existsByUserId(Long userId);
    Practitioner findByUserId(Long userId);
}
