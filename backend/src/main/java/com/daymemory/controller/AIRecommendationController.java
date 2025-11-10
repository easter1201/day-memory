package com.daymemory.controller;

import com.daymemory.domain.dto.AIRecommendationDto;
import com.daymemory.service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/recommendations")
@RequiredArgsConstructor
public class AIRecommendationController {

    private final AIRecommendationService aiRecommendationService;

    /**
     * AI 기반 선물 추천
     * POST /api/ai/recommendations
     */
    @PostMapping
    public ResponseEntity<AIRecommendationDto.RecommendResponse> recommendGifts(
            @RequestBody AIRecommendationDto.RecommendRequest request) {
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(request);
        return ResponseEntity.ok(response);
    }
}
