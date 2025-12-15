package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Booking;
import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.repo.BookingRepository;
import com.wellness.wellness_backend.repo.PractitionerRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PractitionerRepository practitionerRepository;

    public BookingService(BookingRepository bookingRepository,
                          PractitionerRepository practitionerRepository) {
        this.bookingRepository = bookingRepository;
        this.practitionerRepository = practitionerRepository;
    }

    // =====================================================
    // CREATE BOOKING (CORE OF MILESTONE-2)
    // =====================================================
    public Booking createBooking(Long userId,
                                 Long practitionerId,
                                 LocalDateTime slot) {

        // 1. Validate practitioner
        Practitioner practitioner = practitionerRepository
                .findById(practitionerId)
                .orElseThrow(() ->
                        new RuntimeException("Practitioner not found"));

        // 2. Practitioner must be verified
        if (!practitioner.isVerified()) {
            throw new RuntimeException("Practitioner is not verified");
        }

        // 3. Create booking (LOCK ownership + status)
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPractitionerId(practitionerId);
        booking.setSlot(slot);
        booking.setStatus("CREATED"); // force initial state

        return bookingRepository.save(booking);
    }

    // =====================================================
    // READ APIS (SIMPLE & SAFE)
    // =====================================================
    public Booking getById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> getByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getByPractitionerId(Long practitionerId) {
        return bookingRepository.findByPractitionerId(practitionerId);
    }

    // =====================================================
    // UPDATE BOOKING (RESTRICTED)
    // =====================================================
    public Booking updateBooking(Long id,
                                 LocalDateTime newSlot,
                                 String newStatus) {

        return bookingRepository.findById(id).map(existing -> {

            if (newSlot != null) {
                existing.setSlot(newSlot);
            }

            // allow only safe transitions
            if (newStatus != null) {
                if (!List.of("CONFIRMED", "CANCELLED", "COMPLETED")
                        .contains(newStatus)) {
                    throw new RuntimeException("Invalid booking status");
                }
                existing.setStatus(newStatus);
            }

            return bookingRepository.save(existing);

        }).orElse(null);
    }

    // =====================================================
    // CANCEL BOOKING (SOFT CANCEL)
    // =====================================================
    public boolean cancelBooking(Long id) {

        Optional<Booking> opt = bookingRepository.findById(id);
        if (opt.isEmpty()) return false;

        Booking booking = opt.get();
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        return true;
    }

    // =====================================================
    // OWNERSHIP CHECK (FOR CONTROLLER AUTHORIZATION)
    // =====================================================
    public boolean isOwner(Long bookingId, Long userId) {

        if (bookingId == null || userId == null) return false;

        return bookingRepository.findById(bookingId)
                .map(b -> userId.equals(b.getUserId()))
                .orElse(false);
    }
}
