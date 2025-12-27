package com.wellness.wellness_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(optional = false)
    @JoinColumn(name = "practitioner_id")
    private Practitioner practitioner;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Answer() {}

    public Long getId() { return id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) {
        this.question = question;
    }

    public Practitioner getPractitioner() { return practitioner; }
    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
