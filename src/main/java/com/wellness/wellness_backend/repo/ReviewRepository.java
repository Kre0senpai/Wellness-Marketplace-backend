package com.wellness.wellness_backend.repo;

import com.wellness.wellness_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    List<Review> findByPractitionerId(Long practitionerId);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    Optional<Review> findByUserIdAndPractitionerId(Long userId, Long practitionerId);
}
