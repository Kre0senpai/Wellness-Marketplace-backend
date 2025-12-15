package com.wellness.wellness_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // store references by id (optional)
    private Long userId;
    private Long practitionerId;
    
    private LocalDateTime slot;       // appointment time
    private String status;            // CREATED, CONFIRMED, CANCELLED
    private String notes;

    public Booking() {}

    // -------------------- GETTERS & SETTERS --------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPractitionerId() { return practitionerId; }
    public void setPractitionerId(Long practitionerId) { this.practitionerId = practitionerId; }

    public LocalDateTime getSlot() { return slot; }
    public void setSlot(LocalDateTime slot) { this.slot = slot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
