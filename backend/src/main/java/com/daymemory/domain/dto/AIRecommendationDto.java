package com.daymemory.domain.dto;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AIRecommendationDto {

    @Schema(description = "AI 선물 추천 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendRequest {
        @Schema(description = "이벤트 ID", example = "1", required = true)
        private Long eventId;

        @Schema(description = "예산 (원)", example = "50000", required = true)
        private Integer budget;

        @Schema(description = "선호 카테고리 목록", example = "[\"FLOWER\", \"COSMETICS\"]", required = true)
        private List<String> preferredCategories;

        @Schema(description = "받는 사람 성별", example = "FEMALE")
        private String recipientGender;

        @Schema(description = "받는 사람 나이", example = "25")
        private Integer recipientAge;

        @Schema(description = "제외할 선물 정보", example = "꽃은 빼줘, 향수는 이미 선물했어")
        private String additionalMessage;
    }

    @Schema(description = "AI 선물 추천 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendResponse {
        @Schema(description = "추천 ID")
        private Long id;

        @Schema(description = "추천 선물 목록")
        private List<GiftRecommendation> recommendations;

        @Schema(description = "이벤트 제목", example = "엄마 생신")
        private String eventTitle;

        @Schema(description = "이벤트 타입", example = "BIRTHDAY")
        private Event.EventType eventType;

        @Schema(description = "이벤트까지 남은 일수", example = "30")
        private int daysUntilEvent;

        @Schema(description = "받는 사람 이름", example = "엄마")
        private String recipientName;

        @Schema(description = "예산", example = "100000")
        private Integer budget;

        @Schema(description = "추천 상태", example = "COMPLETED")
        private String status;

        @Schema(description = "생성 일시", example = "2024-01-01T12:00:00")
        private String createdAt;

        @Schema(description = "AI 추천 선물 목록 (recommendations와 동일)")
        private List<GiftRecommendation> aiRecommendations;

        @Schema(description = "사용자가 저장한 선물 목록")
        private List<GiftRecommendation> userSavedGifts;
    }

    @Schema(description = "AI 추천 선물 정보")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftRecommendation {
        @Schema(description = "선물 이름", example = "고급 주방용품 세트")
        private String name;

        @Schema(description = "선물 설명", example = "프리미엄 스테인리스 냄비 세트")
        private String description;

        @Schema(description = "추천 이유", example = "요리를 좋아하시는 어머니께 실용적이면서 고급스러운 선물입니다")
        private String reason;

        @Schema(description = "예상 가격 (원)", example = "150000")
        private Integer estimatedPrice;

        @Schema(description = "선물 카테고리", example = "OTHER")
        private GiftItem.GiftCategory category;

        @Schema(description = "구매 링크", example = "https://example.com/product")
        private String purchaseLink;

        @Schema(description = "사용자가 이미 저장한 선물 여부", example = "false")
        private Boolean isUserSaved;

        @Schema(description = "저장된 선물 ID (저장된 경우)", example = "5")
        private Long savedGiftId;
    }
}
