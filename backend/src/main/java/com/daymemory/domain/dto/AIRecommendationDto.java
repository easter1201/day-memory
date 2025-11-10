package com.daymemory.domain.dto;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AIRecommendationDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendRequest {
        private Long eventId;           // 이벤트 ID
        private Event.EventType eventType;  // 이벤트 타입 (이벤트 ID 없을 경우)
        private String relationship;    // 관계 (예: 연인, 친구, 가족)
        private Integer minBudget;      // 최소 예산
        private Integer maxBudget;      // 최대 예산
        private String additionalInfo;  // 추가 정보 (취미, 선호도 등)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendResponse {
        private List<GiftRecommendation> recommendations;
        private String eventTitle;
        private Event.EventType eventType;
        private int daysUntilEvent;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftRecommendation {
        private String name;            // 선물 이름
        private String description;     // 선물 설명
        private String reason;          // 추천 이유
        private Integer estimatedPrice; // 예상 가격
        private GiftItem.GiftCategory category; // 카테고리
        private String purchaseLink;    // 구매 링크 (선택)
    }
}
