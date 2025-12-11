package com.wellness.wellness_backend.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import com.wellness.wellness_backend.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	    List<Booking> findByUserId(Long userId);
	    List<Booking> findByUserEmail(String userEmail);

	    List<Booking> findByPractitionerId(Long practitionerId);
	    List<Booking> findByPractitionerEmail(String practitionerEmail);
}

