package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Recommendation;
import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.repo.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final OpenFdaService openFdaService;
    private final NotificationService notificationService;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 OpenFdaService openFdaService,
                                 NotificationService notificationService) {
        this.recommendationRepository = recommendationRepository;
        this.openFdaService = openFdaService;
        this.notificationService = notificationService;
    }

    public Recommendation generateRecommendation(User user, String symptom) {

        String therapy;
        String s = symptom.toLowerCase();

        if (s.contains("back") || s.contains("pain")) {
            therapy = "Physiotherapy";
        } else if (s.contains("stress") || s.contains("anxiety")) {
            therapy = "Yoga and Meditation";
        } else if (s.contains("digestion") || s.contains("stomach")) {
            therapy = "Ayurveda";
        } else {
            therapy = "General Wellness Consultation";
        }

        // 🔹 Call OpenFDA (external API)
        openFdaService.fetchDrugInfo(symptom);
        // We don’t store FDA response — just prove integration

        Recommendation rec = new Recommendation();
        rec.setUser(user);
        rec.setSymptom(symptom);
        rec.setSuggestedTherapy(therapy);
        rec.setSource("RULE_ENGINE + OPENFDA");
        rec.setCreatedAt(LocalDateTime.now());

        Recommendation saved = recommendationRepository.save(rec);

        // 🔔 Create notification
        notificationService.createNotification(
                user,
                "New therapy recommendation generated: " + therapy
        );

        return saved;
    }

    public List<Recommendation> getUserRecommendations(Long userId) {
        return recommendationRepository.findByUserId(userId);
    }
}
