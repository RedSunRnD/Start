package com.example.start.controller;

import com.example.start.dto.Recommendation;
import com.example.start.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/recommendation/{userId}")
    public Map<String, Object> getRecommendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = service.getRecommendationsForUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId.toString());
        response.put("recommendations", recommendations);

        return response;
    }
}