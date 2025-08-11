package com.example.start.controller;

import com.example.start.entity.Rule;
import com.example.start.repository.RuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleRepository ruleRepository;

    public RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @PostMapping
    public ResponseEntity<Rule> createRule(@RequestBody Rule rule) {
        if (rule.getId() == null) {
            rule.setId(UUID.randomUUID());
        }
        Rule saved = ruleRepository.save(rule);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<Map<String, List<Rule>>> getAllRules() {
        Map<String, List<Rule>> response = new HashMap<>();
        response.put("data", ruleRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteRule(@PathVariable String productId) {
        ruleRepository.findByProductId(productId).ifPresent(ruleRepository::delete);
        return ResponseEntity.noContent().build();
    }
}