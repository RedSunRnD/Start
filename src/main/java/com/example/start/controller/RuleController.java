package com.example.start.controller;

import com.example.start.entity.Rule;
import com.example.start.entity.RuleStats;
import com.example.start.repository.RuleRepository;
import com.example.start.repository.RuleStatsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleRepository ruleRepository;
    private final RuleStatsRepository ruleStatsRepository;

    public RuleController(RuleRepository ruleRepository, RuleStatsRepository ruleStatsRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleStatsRepository = ruleStatsRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Rule createRule(@RequestBody Rule rule) {
        if (rule.getId() == null) {
            rule.setId(UUID.randomUUID());
        }
        return ruleRepository.save(rule);
    }

    @GetMapping
    public ResponseEntity<Map<String, List<Rule>>> getAllRules() {
        Map<String, List<Rule>> response = new HashMap<>();
        response.put("data", ruleRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable String productId) {
        ruleRepository.findByProductId(productId).ifPresent(rule -> {
            ruleStatsRepository.findByRuleId(rule.getId()).ifPresent(ruleStatsRepository::delete);
            ruleRepository.delete(rule);
        });
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getRuleStats() {
        List<Rule> rules = ruleRepository.findAll();
        List<Map<String, Object>> stats = new ArrayList<>();
        for (Rule rule : rules) {
            long count = ruleStatsRepository.findByRuleId(rule.getId())
                    .map(RuleStats::getCount)
                    .orElse(0L);
            Map<String, Object> stat = new HashMap<>();
            stat.put("rule_id", rule.getId().toString());
            stat.put("count", count);
            stats.add(stat);
        }
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("stats", stats);
        return ResponseEntity.ok(response);
    }
}