package com.example.start.controller;

import com.example.start.repository.RecommendationRepository;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final RecommendationRepository recommendationRepository;
    private final BuildProperties buildProperties;

    public ManagementController(RecommendationRepository recommendationRepository, BuildProperties buildProperties) {
        this.recommendationRepository = recommendationRepository;
        this.buildProperties = buildProperties;
    }

    @PostMapping("/clear-caches")
    @ResponseStatus(HttpStatus.OK)
    public void clearCaches() {
        recommendationRepository.clearCaches();
    }

    @GetMapping("/info")
    public Map<String, String> getServiceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("name", buildProperties.getName());
        info.put("version", buildProperties.getVersion());
        return info;
    }
}