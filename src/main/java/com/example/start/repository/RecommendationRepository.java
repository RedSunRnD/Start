package com.example.start.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final Cache<String, Boolean> userHasProductCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES) // Настройте по нужде
            .maximumSize(1000)
            .build();

    private final Cache<String, Boolean> activeUserCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<String, Double> sumTransactionsCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean userHasProductType(UUID userId, String productType) {
        String key = userId + ":" + productType + ":has";
        return userHasProductCache.get(key, k -> {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM transactions t JOIN products p ON t.product_id = p.id " +
                            "WHERE t.user_id = ? AND p.type = ?",
                    Integer.class,
                    userId, productType);
            return count != null && count > 0;
        });
    }

    public boolean isActiveUserOfProductType(UUID userId, String productType) {
        String key = userId + ":" + productType + ":active";
        return activeUserCache.get(key, k -> {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM transactions t JOIN products p ON t.product_id = p.id " +
                            "WHERE t.user_id = ? AND p.type = ?",
                    Integer.class,
                    userId, productType);
            return count != null && count >= 5;
        });
    }

    public double getSumTransactions(UUID userId, String productType, String transactionType) {
        String key = userId + ":" + productType + ":" + transactionType + ":sum";
        return sumTransactionsCache.get(key, k -> {
            Double sum = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t JOIN products p ON t.product_id = p.id " +
                            "WHERE t.user_id = ? AND p.type = ? AND t.type = ?",
                    Double.class,
                    userId, productType, transactionType);
            return sum != null ? sum : 0;
        });
    }
}