package com.wellness.wellness_backend.repo;

import com.wellness.wellness_backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // BASIC
    List<Booking> findByUserId(Long userId);
    List<Booking> findByPractitionerId(Long practitionerId);

    // USER DASHBOARD
    List<Booking> findByUserIdAndSlotAfterOrderBySlotAsc(
            Long userId, LocalDateTime now);

    List<Booking> findByUserIdAndSlotBeforeOrderBySlotDesc(
            Long userId, LocalDateTime now);

    // PRACTITIONER CALENDAR
    List<Booking> findByPractitionerIdOrderBySlotAsc(Long practitionerId);
    
    List<Booking> findByStatusAndSlotBetween(
            String status,
            LocalDateTime start,
            LocalDateTime end
    );

}
