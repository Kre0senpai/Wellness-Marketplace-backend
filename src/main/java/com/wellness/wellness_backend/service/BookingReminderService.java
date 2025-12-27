package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Booking;
import com.wellness.wellness_backend.repo.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingReminderService {

    private final BookingRepository bookingRepository;

    public BookingReminderService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Runs every 10 minutes
    @Scheduled(cron = "0 */10 * * * *")
    public void remindUpcomingBookings() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in30Minutes = now.plusMinutes(30);

        List<Booking> bookings =
                bookingRepository.findByStatusAndSlotBetween(
                        "CONFIRMED",
                        now,
                        in30Minutes
                );

        for (Booking b : bookings) {
            System.out.println(
                "🔔 Reminder: Booking ID " + b.getId()
                        + " at " + b.getSlot()
            );
        }
    }
}
