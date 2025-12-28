package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.*;
import com.wellness.wellness_backend.service.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    public RecommendationController(RecommendationService recommendationService,
                                    UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    // generate recommendation
    @PostMapping
    public Recommendation generate(@RequestParam String symptom,
                                   Principal principal) {

        User user = userService.getByEmail(principal.getName());
        return recommendationService.generateRecommendation(user, symptom);
    }

    // view my recommendations
    @GetMapping("/my")
    public List<Recommendation> myRecommendations(Principal principal) {

        User user = userService.getByEmail(principal.getName());
        return recommendationService.getUserRecommendations(user.getId());
    }
}
