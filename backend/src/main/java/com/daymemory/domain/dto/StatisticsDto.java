package com.daymemory.domain.dto;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

public class StatisticsDto {

    @Schema(description = "이벤트 통계 정보")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventStatistics {
        @Schema(description = "월별 이벤트 수 (키: YYYY-MM)", example = "{\"2025-11\": 3, \"2025-12\": 5}")
        private Map<String, Integer> monthlyEventCount;

        @Schema(description = "이벤트 타입별 분포", example = "{\"BIRTHDAY\": 10, \"ANNIVERSARY\": 5}")
        private Map<Event.EventType, Integer> eventTypeDistribution;

        @Schema(description = "총 이벤트 수", example = "25")
        private int totalEvents;
    }

    @Schema(description = "선물 구매 통계 정보")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftStatistics {
        @Schema(description = "총 구매 선물 수", example = "15")
        private int totalPurchasedGifts;

        @Schema(description = "카테고리별 구매 통계", example = "{\"ELECTRONICS\": 5, \"FASHION\": 3}")
        private Map<GiftItem.GiftCategory, Integer> categoryDistribution;

        @Schema(description = "총 지출 금액 (원)", example = "1500000")
        private int totalSpentAmount;

        @Schema(description = "평균 선물 가격 (원)", example = "100000")
        private int averageGiftPrice;
    }

    @Schema(description = "리마인더 발송 통계 정보")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReminderStatistics {
        @Schema(description = "발송 성공 건수", example = "45")
        private int sentCount;

        @Schema(description = "발송 실패 건수", example = "2")
        private int failedCount;

        @Schema(description = "성공률 (%)", example = "95.7")
        private double successRate;

        @Schema(description = "일별 발송 통계 (키: YYYY-MM-DD)")
        private Map<String, DailyReminderStats> dailyStats;
    }

    @Schema(description = "일별 리마인더 발송 통계")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyReminderStats {
        @Schema(description = "날짜 (YYYY-MM-DD)", example = "2025-11-11")
        private String date;

        @Schema(description = "발송 성공 건수", example = "5")
        private int sentCount;

        @Schema(description = "발송 실패 건수", example = "0")
        private int failedCount;
    }

    @Schema(description = "캘린더 이벤트 정보")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarEvent {
        @Schema(description = "이벤트 ID", example = "1")
        private Long eventId;

        @Schema(description = "이벤트 제목", example = "엄마 생신")
        private String title;

        @Schema(description = "이벤트 날짜", example = "2025-12-25")
        private LocalDate date;

        @Schema(description = "이벤트 타입", example = "BIRTHDAY")
        private Event.EventType type;

        @Schema(description = "트래킹 활성화 여부", example = "true")
        private boolean isTracking;

        @Schema(description = "남은 일수", example = "44")
        private int daysRemaining;
    }
}
