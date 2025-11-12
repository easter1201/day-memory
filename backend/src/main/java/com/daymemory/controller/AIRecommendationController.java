package com.daymemory.controller;

import com.daymemory.domain.dto.AIRecommendationDto;
import com.daymemory.service.AIRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "AI Recommendation", description = "AI 기반 선물 추천 API - Claude AI를 활용하여 개인화된 선물을 추천합니다.")
@RestController
@RequestMapping("/api/ai/recommendations")
@RequiredArgsConstructor
public class AIRecommendationController {

    private final AIRecommendationService aiRecommendationService;

    /**
     * AI 추천 이력 조회 (임시 - 빈 목록 반환)
     * GET /api/ai/recommendations
     */
    @Operation(summary = "AI 추천 이력 조회", description = "AI 추천 이력을 조회합니다. 현재는 실시간 추천만 지원하여 빈 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AIRecommendationDto.RecommendResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<AIRecommendationDto.RecommendResponse>> getRecommendations() {
        // 현재는 추천 이력 저장 기능이 없으므로 빈 목록 반환
        return ResponseEntity.ok(new ArrayList<>());
    }

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
