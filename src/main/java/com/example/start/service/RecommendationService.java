package com.example.start.service;

import com.example.start.dto.Recommendation;
import com.example.start.entity.Rule;
import com.example.start.repository.RuleRepository;
import com.example.start.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> fixedRuleSets;
    private final RuleRepository ruleRepository;
    private final DynamicRuleEvaluator dynamicEvaluator;

    public RecommendationService(List<RecommendationRuleSet> fixedRuleSets, RuleRepository ruleRepository, DynamicRuleEvaluator dynamicEvaluator) {
        this.fixedRuleSets = fixedRuleSets;
        this.ruleRepository = ruleRepository;
        this.dynamicEvaluator = dynamicEvaluator;
    }

    public List<Recommendation> getRecommendationsForUser(UUID userId) {
        List<Recommendation> fixedRecommendations = fixedRuleSets.stream()
                .map(rule -> rule.getRecommendationForUser(userId))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        List<Recommendation> dynamicRecommendations = ruleRepository.findAll().stream()
                .map(rule -> dynamicEvaluator.evaluateRuleForUser(rule, userId))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        fixedRecommendations.addAll(dynamicRecommendations);
        return fixedRecommendations;
    }
}