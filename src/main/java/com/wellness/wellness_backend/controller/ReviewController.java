package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.*;
import com.wellness.wellness_backend.service.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;
    private final PractitionerService practitionerService;

    // MANUAL constructor injection
    public ReviewController(ReviewService reviewService,
                            UserService userService,
                            ProductService productService,
                            PractitionerService practitionerService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.productService = productService;
        this.practitionerService = practitionerService;
    }

    @PostMapping("/product/{productId}")
    public Review addProductReview(@PathVariable Long productId,
                                   @RequestParam int rating,
                                   @RequestParam(required = false) String comment,
                                   Principal principal) {

        User user = userService.getByEmail(principal.getName());
        Product product = productService.getById(productId);

        return reviewService.createProductReview(user, product, rating, comment);
    }

    @PostMapping("/practitioner/{practitionerId}")
    public Review addPractitionerReview(@PathVariable Long practitionerId,
                                        @RequestParam int rating,
                                        @RequestParam(required = false) String comment,
                                        Principal principal) {

        User user = userService.getByEmail(principal.getName());
        Practitioner practitioner =
                practitionerService.getById(practitionerId);

        return reviewService.createPractitionerReview(
                user, practitioner, rating, comment);
    }

    @GetMapping("/product/{productId}")
    public List<Review> getProductReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }

    @GetMapping("/practitioner/{practitionerId}")
    public List<Review> getPractitionerReviews(
            @PathVariable Long practitionerId) {
        return reviewService.getPractitionerReviews(practitionerId);
    }
}
