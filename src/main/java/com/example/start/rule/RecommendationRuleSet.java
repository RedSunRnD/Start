package com.example.start.rule;

import com.example.start.dto.Recommendation;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<Recommendation> getRecommendationForUser(UUID userId);
}