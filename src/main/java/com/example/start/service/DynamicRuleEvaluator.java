package com.example.start.service;

import com.example.start.dto.Recommendation;
import com.example.start.entity.Rule;
import com.example.start.entity.RuleStats;
import com.example.start.repository.RecommendationRepository;
import com.example.start.repository.RuleStatsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DynamicRuleEvaluator {

    private final RecommendationRepository repository;
    private final RuleStatsRepository ruleStatsRepository;

    public DynamicRuleEvaluator(RecommendationRepository repository, RuleStatsRepository ruleStatsRepository) {
        this.repository = repository;
        this.ruleStatsRepository = ruleStatsRepository;
    }

    public Optional<Recommendation> evaluateRuleForUser(Rule dynamicRule, UUID userId) {
        boolean allTrue = dynamicRule.getRule().stream()
                .allMatch(query -> evaluateQuery(query, userId));
        if (allTrue) {
            RuleStats stats = ruleStatsRepository.findByRuleId(dynamicRule.getId())
                    .orElseGet(() -> new RuleStats(dynamicRule.getId()));
            stats.incrementCount();
            ruleStatsRepository.save(stats);
            return Optional.of(new Recommendation(dynamicRule.getProductId(), dynamicRule.getProductName(), dynamicRule.getProductText()));
        }
        return Optional.empty();
    }

    private boolean evaluateQuery(Rule.RuleQuery queryObj, UUID userId) {
        boolean result;
        String queryType = queryObj.getQuery();
        List<String> args = queryObj.getArguments();
        switch (queryType) {
            case "USER_OF":
                result = repository.userHasProductType(userId, args.get(0));
                break;
            case "ACTIVE_USER_OF":
                result = repository.isActiveUserOfProductType(userId, args.get(0));
                break;
            case "TRANSACTION_SUM_COMPARE":
                String productType = args.get(0);
                String txType = args.get(1);
                String operator = args.get(2);
                double constant = Double.parseDouble(args.get(3));
                double sum = repository.getSumTransactions(userId, productType, txType);
                result = compare(sum, operator, constant);
                break;
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                String prodType = args.get(0);
                String op = args.get(1);
                double depositSum = repository.getSumTransactions(userId, prodType, "DEPOSIT");
                double withdrawSum = repository.getSumTransactions(userId, prodType, "WITHDRAWAL");
                result = compare(depositSum, op, withdrawSum);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type: " + queryType);
        }
        return queryObj.isNegate() ? !result : result;
    }

    private boolean compare(double left, String operator, double right) {
        return switch (operator) {
            case ">" -> left > right;
            case "<" -> left < right;
            case "=" -> left == right;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}