package com.example.start.repository;

import com.example.start.entity.RuleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStats, UUID> {
    Optional<RuleStats> findByRuleId(UUID ruleId);
}