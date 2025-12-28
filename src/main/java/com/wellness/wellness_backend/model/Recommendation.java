package com.wellness.wellness_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who requested recommendation
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // symptom entered by user
    @Column(nullable = false, length = 500)
    private String symptom;

    // therapy suggested by system
    @Column(nullable = false, length = 255)
    private String suggestedTherapy;

    // where this recommendation came from
    // ex: RULE_ENGINE, OPENFDA, MOCK_WHO
    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Recommendation() {}

    // ===== getters & setters =====

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getSuggestedTherapy() {
        return suggestedTherapy;
    }

    public void setSuggestedTherapy(String suggestedTherapy) {
        this.suggestedTherapy = suggestedTherapy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
