package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Booking;
import com.wellness.wellness_backend.service.BookingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;
    public BookingController(BookingService service) { this.service = service; }

    /**
     * Create a booking
     * Any authenticated user can create a booking; the booking owner is set from the JWT (authentication.name).
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody Booking b, Authentication auth) {
        // set booking owner (email) from authenticated principal
        b.setUserEmail(auth.getName());
        Booking saved = service.create(b);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * List bookings
     * - If called with userId param, returns bookings for that user.
     * - If called with practitionerId param, returns bookings for that practitioner.
     * - If no params provided: admins get all bookings; non-admins get only their bookings.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<Booking>> getAll(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "practitionerId", required = false) Long practitionerId,
            Authentication auth) {

        // If querying by userId: allow if admin or requesting own userId
        if (userId != null) {
            // admin can query any user
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            // if not admin, ensure the authenticated user corresponds to the requested user
            if (!isAdmin) {
                // assume service can map email -> userId OR you can compare auth.getName() to the stored user email
                // Here we'll fetch by email instead for safety if service provides getByEmail-based retrieval
                // fallback: deny if non-admin and userId param used
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(service.getByUserId(userId));
        }

        // If querying by practitionerId: only practitioner or admin can call this
        if (practitionerId != null) {
            boolean isPractitionerOrAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PRACTITIONER") || a.getAuthority().equals("ROLE_ADMIN"));
            if (!isPractitionerOrAdmin) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            return ResponseEntity.ok(service.getByPractitionerId(practitionerId));
        }

        // No params: if admin -> return all; otherwise return bookings for current user (by email)
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok(service.getAll());
        } else {
            // return bookings for the current authenticated user by their email
            return ResponseEntity.ok(service.getByUserEmail(auth.getName()));
        }
    }

    /**
     * Get booking by id
     * Only owner or admin can view a booking directly.
     */
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Booking b = service.getById(id);
        if (b == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(b);
    }

    /**
     * Update booking
     * Only owner or admin can update.
     * Note: you may want to restrict what fields can be changed after creation (status, cancel reason etc.)
     */
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Booking updated) {
        Booking b = service.update(id, updated);
        if (b == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(b);
    }

    /**
     * Cancel/delete booking
     * Only owner or admin can cancel.
     */
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = service.cancel(id); // or service.delete(id) depending on your service API
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper endpoint: bookings for the currently authenticated practitioner (if practitioner)
     * This is convenient for practitioner dashboards.
     */
    @PreAuthorize("hasRole('PRACTITIONER') or hasRole('ADMIN')")
    @GetMapping("/mine")
    public ResponseEntity<List<Booking>> myPractitionerBookings(Authentication auth) {
        // You must have a way to map practitioner email to practitionerId inside the service.
        List<Booking> bookings = service.findByPractitionerEmail(auth.getName());
        return ResponseEntity.ok(bookings);
    }
}
