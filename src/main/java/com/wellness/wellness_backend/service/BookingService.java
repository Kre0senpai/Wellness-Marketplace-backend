package com.wellness.wellness_backend.service;

import org.springframework.stereotype.Service;
import com.wellness.wellness_backend.repo.BookingRepository;
import com.wellness.wellness_backend.model.Booking;

import java.util.List;
import java.util.Optional;

@Service("bookingService")
public class BookingService {

    private final BookingRepository repo;
    public BookingService(BookingRepository repo) { this.repo = repo; }

    /**
     * Create booking. Controller should set userEmail before calling this.
     */
    public Booking create(Booking b) {
        if (b.getStatus() == null) b.setStatus("CREATED");
        return repo.save(b);
    }

    public List<Booking> getAll() { return repo.findAll(); }

    public Booking getById(Long id) { return repo.findById(id).orElse(null); }

    public Booking update(Long id, Booking updated) {
        return repo.findById(id).map(existing -> {
            if (updated.getSlot() != null) existing.setSlot(updated.getSlot());
            if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
            if (updated.getNotes() != null) existing.setNotes(updated.getNotes());
            // do NOT overwrite owner fields (userEmail/practitionerEmail) here
            return repo.save(existing);
        }).orElse(null);
    }

    public List<Booking> getByUserId(Long userId) { return repo.findByUserId(userId); }

    public List<Booking> getByPractitionerId(Long practitionerId) { return repo.findByPractitionerId(practitionerId); }

    // ----------------- NEW METHODS REQUIRED BY CONTROLLER -----------------

    /**
     * Return bookings belonging to the user identified by email.
     * Used so non-admin callers can fetch their own bookings.
     */
    public List<Booking> getByUserEmail(String userEmail) {
        return repo.findByUserEmail(userEmail);
    }

    /**
     * Return bookings linked to a practitioner by email (for /mine endpoint).
     */
    public List<Booking> findByPractitionerEmail(String practitionerEmail) {
        return repo.findByPractitionerEmail(practitionerEmail);
    }

    /**
     * Cancel a booking. This performs a soft-cancel if Booking has `status` field,
     * otherwise it performs a hard delete.
     * Returns true if booking existed and was cancelled/deleted.
     */
    public boolean cancel(Long id) {
        Optional<Booking> opt = repo.findById(id);
        if (opt.isEmpty()) return false;
        Booking b = opt.get();
        // try soft-cancel if model supports status, otherwise hard delete
        try {
            b.setStatus("CANCELLED");
            repo.save(b);
            return true;
        } catch (NoSuchMethodError | RuntimeException ex) {
            // fallback to hard delete
            repo.deleteById(id);
            return true;
        }
    }

    /**
     * Owner check used by @PreAuthorize SpEL in controllers.
     * Returns true if the booking exists and the provided userEmail matches booking.userEmail.
     */
    public boolean isOwner(Long bookingId, String userEmail) {
        if (bookingId == null || userEmail == null) return false;
        Optional<Booking> opt = repo.findById(bookingId);
        return opt.isPresent() && userEmail.equals(opt.get().getUserEmail());
    }
}
