package com.wellness.wellness_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "practitioner_availability")
public class PractitionerAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long practitionerId;

    private LocalDateTime slot;

    @Column(nullable = false)
    private boolean booked = false;

    public Long getId() { return id; }

    public Long getPractitionerId() { return practitionerId; }
    public void setPractitionerId(Long practitionerId) {
        this.practitionerId = practitionerId;
    }

    public LocalDateTime getSlot() { return slot; }
    public void setSlot(LocalDateTime slot) {
        this.slot = slot;
    }

    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
