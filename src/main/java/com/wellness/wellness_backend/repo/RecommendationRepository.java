package com.wellness.wellness_backend.repo;

import com.wellness.wellness_backend.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository
        extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserId(Long userId);
}
