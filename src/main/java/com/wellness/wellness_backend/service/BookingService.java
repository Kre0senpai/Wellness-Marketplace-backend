package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Booking;
import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.repo.BookingRepository;
import com.wellness.wellness_backend.repo.PractitionerRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("bookingService")
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PractitionerRepository practitionerRepository;
    private final PractitionerAvailabilityService availabilityService;

    public BookingService(BookingRepository bookingRepository,
                          PractitionerRepository practitionerRepository,
                          PractitionerAvailabilityService availabilityService) {
        this.bookingRepository = bookingRepository;
        this.practitionerRepository = practitionerRepository;
        this.availabilityService = availabilityService;
    }

    // =========================
    // CREATE BOOKING
    // =========================
    public Booking createBooking(Long userId,
                                 Long practitionerId,
                                 LocalDateTime slot) {

        Practitioner practitioner = practitionerRepository
                .findById(practitionerId)
                .orElseThrow(() -> new RuntimeException("Practitioner not found"));

        if (!practitioner.isVerified()) {
            throw new RuntimeException("Practitioner is not verified");
        }

        availabilityService.reserveSlot(practitionerId, slot);

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPractitionerId(practitionerId);
        booking.setSlot(slot);
        booking.setStatus("CREATED");

        return bookingRepository.save(booking);
    }

    // =========================
    // USER DASHBOARD
    // =========================
    public List<Booking> getUserUpcoming(Long userId) {
        return bookingRepository
                .findByUserIdAndSlotAfterOrderBySlotAsc(
                        userId, LocalDateTime.now());
    }

    public List<Booking> getUserPast(Long userId) {
        return bookingRepository
                .findByUserIdAndSlotBeforeOrderBySlotDesc(
                        userId, LocalDateTime.now());
    }

    // =========================
    // PRACTITIONER CALENDAR
    // =========================
    public List<Booking> getPractitionerCalendar(Long userId) {

        Practitioner practitioner =
                practitionerRepository.findByUserId(userId);

        if (practitioner == null) {
            throw new RuntimeException("Practitioner profile not found");
        }

        return bookingRepository
                .findByPractitionerIdOrderBySlotAsc(practitioner.getId());
    }

    // =========================
    // STATUS TRANSITIONS
    // =========================
    public void confirmBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"CREATED".equals(booking.getStatus())) {
            throw new RuntimeException("Only CREATED bookings can be confirmed");
        }

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
    }

    public void completeBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Only CONFIRMED bookings can be completed");
        }

        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

}
