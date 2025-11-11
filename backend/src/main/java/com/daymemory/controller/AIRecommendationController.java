package com.daymemory.controller;

import com.daymemory.domain.dto.AIRecommendationDto;
import com.daymemory.service.AIRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI Recommendation", description = "AI 기반 선물 추천 API - Claude AI를 활용하여 개인화된 선물을 추천합니다.")
@RestController
@RequestMapping("/api/ai/recommendations")
@RequiredArgsConstructor
public class AIRecommendationController {

    private final AIRecommendationService aiRecommendationService;

    /**
     * AI 기반 선물 추천
     * POST /api/ai/recommendations
     */
    @Operation(summary = "AI 기반 선물 추천", description = "이벤트 정보와 수신자 프로필을 기반으로 Claude AI가 개인화된 선물을 추천합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 성공",
                    content = @Content(schema = @Schema(implementation = AIRecommendationDto.RecommendResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "AI 추천 서비스 오류",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AIRecommendationDto.RecommendResponse> recommendGifts(
            @RequestBody AIRecommendationDto.RecommendRequest request) {
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(request);
        return ResponseEntity.ok(response);
    }
}
