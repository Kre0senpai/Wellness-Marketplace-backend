package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.*;
import com.wellness.wellness_backend.repo.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // MANUAL constructor injection (NO Lombok)
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createProductReview(User user, Product product,
                                      int rating, String comment) {

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        reviewRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .ifPresent(r -> {
                    throw new RuntimeException("User already reviewed this product");
                });

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public Review createPractitionerReview(User user, Practitioner practitioner,
                                           int rating, String comment) {

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        reviewRepository.findByUserIdAndPractitionerId(
                user.getId(), practitioner.getId())
                .ifPresent(r -> {
                    throw new RuntimeException("User already reviewed this practitioner");
                });

        Review review = new Review();
        review.setUser(user);
        review.setPractitioner(practitioner);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getPractitionerReviews(Long practitionerId) {
        return reviewRepository.findByPractitionerId(practitionerId);
    }
}
