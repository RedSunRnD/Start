package com.example.start.repository;

import com.example.start.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {
    Optional<Rule> findByProductId(String productId);
}