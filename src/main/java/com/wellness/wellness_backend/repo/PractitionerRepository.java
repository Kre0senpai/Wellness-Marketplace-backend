package com.wellness.wellness_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wellness.wellness_backend.model.Practitioner;

public interface PractitionerRepository extends JpaRepository<Practitioner, Long> {

}
