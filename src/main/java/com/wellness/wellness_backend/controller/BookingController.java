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

    // =========================
    // USER — CREATE BOOKING
    // =========================
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestParam Long practitionerId,
            @RequestParam LocalDateTime slot,
            Authentication auth) {

        AuthUser user = (AuthUser) auth.getPrincipal();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(
                        user.getUserId(), practitionerId, slot));
    }

    // =========================
    // USER DASHBOARD
    // =========================
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my/upcoming")
    public List<Booking> myUpcoming(Authentication auth) {

        AuthUser user = (AuthUser) auth.getPrincipal();
        return bookingService.getUserUpcoming(user.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my/past")
    public List<Booking> myPast(Authentication auth) {

        AuthUser user = (AuthUser) auth.getPrincipal();
        return bookingService.getUserPast(user.getUserId());
    }

    // =========================
    // PRACTITIONER CALENDAR
    // =========================
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/practitioner/calendar")
    public ResponseEntity<List<Booking>> practitionerCalendar(Authentication auth) {

        AuthUser user = (AuthUser) auth.getPrincipal();

        return ResponseEntity.ok(
                bookingService.getPractitionerCalendar(user.getUserId())
        );
    }


    // =========================
    // STATUS ACTIONS
    // =========================
    @PreAuthorize("hasRole('PRACTITIONER')")
    @PutMapping("/{id}/confirm")
    public void confirm(@PathVariable Long id) {
        bookingService.confirmBooking(id);
    }

    @PreAuthorize("hasRole('PRACTITIONER')")
    @PutMapping("/{id}/complete")
    public void complete(@PathVariable Long id) {
        bookingService.completeBooking(id);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancel(@PathVariable Long id) {

        Booking cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelledBooking);
    }
}
