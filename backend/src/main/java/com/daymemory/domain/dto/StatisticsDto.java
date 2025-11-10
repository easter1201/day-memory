package com.daymemory.domain.dto;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

public class StatisticsDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventStatistics {
        // 월별 이벤트 수
        private Map<String, Integer> monthlyEventCount;

        // 이벤트 타입별 분포
        private Map<Event.EventType, Integer> eventTypeDistribution;

        // 총 이벤트 수
        private int totalEvents;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftStatistics {
        // 총 구매 선물 수
        private int totalPurchasedGifts;

        // 카테고리별 구매 통계
        private Map<GiftItem.GiftCategory, Integer> categoryDistribution;

        // 총 지출 금액
        private int totalSpentAmount;

        // 평균 선물 가격
        private int averageGiftPrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReminderStatistics {
        // 발송 성공 건수
        private int sentCount;

        // 발송 실패 건수
        private int failedCount;

        // 성공률 (%)
        private double successRate;

        // 일별 발송 통계
        private Map<String, DailyReminderStats> dailyStats;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyReminderStats {
        private String date;
        private int sentCount;
        private int failedCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarEvent {
        private Long eventId;
        private String title;
        private LocalDate date;
        private Event.EventType type;
        private boolean isTracking;
        private int daysRemaining;
    }
}
