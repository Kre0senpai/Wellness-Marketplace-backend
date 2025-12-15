package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Booking;
import com.wellness.wellness_backend.security.AuthUser;
import com.wellness.wellness_backend.service.BookingService;

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

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ============================
    // CREATE BOOKING (USER)
    // ============================
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestParam Long practitionerId,
            @RequestParam LocalDateTime slot,
            Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        Long userId = user.getUserId();

        Booking booking =
                bookingService.createBooking(userId, practitionerId, slot);

        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    // ============================
    // USER: MY BOOKINGS
    // ============================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResponseEntity<List<Booking>> myBookings(Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        Long userId = user.getUserId();

        return ResponseEntity.ok(bookingService.getByUserId(userId));
    }

    // ============================
    // ADMIN: ALL BOOKINGS
    // ============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<Booking>> allBookings() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    // ============================
    // CANCEL BOOKING (OWNER / ADMIN)
    // ============================
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication)")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled");
    }
}
