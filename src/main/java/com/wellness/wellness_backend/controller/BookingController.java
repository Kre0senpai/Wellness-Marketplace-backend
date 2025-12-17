package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Booking;
import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.security.AuthUser;
import com.wellness.wellness_backend.service.BookingService;
import com.wellness.wellness_backend.repo.PractitionerRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final PractitionerRepository practitionerRepository;

    public BookingController(
            BookingService bookingService,
            PractitionerRepository practitionerRepository
    ) {
        this.bookingService = bookingService;
        this.practitionerRepository = practitionerRepository;
    }

    // =====================================================
    // USER — CREATE BOOKING
    // =====================================================
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestParam Long practitionerId,
            @RequestParam LocalDateTime slot,
            Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();

        Booking booking = bookingService.createBooking(
                user.getUserId(),
                practitionerId,
                slot
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    // =====================================================
    // USER — VIEW OWN BOOKINGS
    // =====================================================
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public ResponseEntity<List<Booking>> myBookings(Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        return ResponseEntity.ok(
                bookingService.getByUserId(user.getUserId())
        );
    }

    // =====================================================
    // PRACTITIONER — VIEW ASSIGNED BOOKINGS
    // =====================================================
    @PreAuthorize("hasRole('PRACTITIONER')")
    @GetMapping("/practitioner")
    public ResponseEntity<List<Booking>> practitionerBookings(Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();

        Practitioner practitioner =
                practitionerRepository.findByUserId(user.getUserId());

        if (practitioner == null) {
            throw new RuntimeException("Practitioner profile not found");
        }

        return ResponseEntity.ok(
                bookingService.getByPractitionerId(practitioner.getId())
        );
    }

    // =====================================================
    // ADMIN — VIEW ALL BOOKINGS
    // =====================================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Booking>> allBookings() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    // =====================================================
    // CANCEL BOOKING (OWNER / ADMIN)
    // =====================================================
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication)")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {

        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled");
    }

    // =====================================================
    // CONFIRM BOOKING (PRACTITIONER / ADMIN)
    // =====================================================
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isPractitionerBooking(#id, authentication)")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id) {

        bookingService.confirmBooking(id);
        return ResponseEntity.ok("Booking confirmed");
    }

    // =====================================================
    // COMPLETE BOOKING (PRACTITIONER / ADMIN)
    // =====================================================
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isPractitionerBooking(#id, authentication)")
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable Long id) {

        bookingService.completeBooking(id);
        return ResponseEntity.ok("Booking completed");
    }
}
