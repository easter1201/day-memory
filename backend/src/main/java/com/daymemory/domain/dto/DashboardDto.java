package com.daymemory.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "대시보드 정보 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    @Schema(description = "다가오는 이벤트 개수 (30일 이내)", example = "5")
    private int upcomingEventsCount;

    @Schema(description = "미구매 선물 개수", example = "3")
    private int unpurchasedGiftsCount;

    @Schema(description = "최근 리마인더 발송 현황")
    private RecentReminderStatus recentReminderStatus;

    @Schema(description = "이번 달 이벤트 개수", example = "2")
    private int thisMonthEventsCount;

    @Schema(description = "다가오는 이벤트 목록 (최대 5개)")
    private List<EventDto.Response> upcomingEvents;

    @Schema(description = "오늘 발송 예정인 리마인더 목록")
    private List<TodayReminderDto> todayReminders;

    @Schema(description = "최근 리마인더 발송 현황")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentReminderStatus {
        @Schema(description = "최근 7일간 발송 성공 건수", example = "10")
        private int sentCount;

        @Schema(description = "최근 7일간 발송 실패 건수", example = "1")
        private int failedCount;

        @Schema(description = "마지막 발송 시간", example = "2025-11-11T09:00:00")
        private String lastSentAt;
    }

    @Schema(description = "오늘 발송 예정인 리마인더")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayReminderDto {
        @Schema(description = "이벤트 ID", example = "1")
        private Long eventId;

        @Schema(description = "이벤트 제목", example = "어머니 생신")
        private String eventTitle;

        @Schema(description = "받는 사람 이름", example = "어머니")
        private String recipientName;

        @Schema(description = "이벤트 날짜", example = "2025-11-20")
        private String eventDate;

        @Schema(description = "이벤트까지 남은 일수", example = "7")
        private int daysUntilEvent;
    }
}
