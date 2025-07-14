package com.example.start.service;

import com.example.start.dto.Recommendation;
import com.example.start.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<Recommendation> getRecommendationsForUser(UUID userId) {
        return ruleSets.stream()
                .map(rule -> rule.getRecommendationForUser(userId))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }
}