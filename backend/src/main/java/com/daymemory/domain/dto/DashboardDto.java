package com.daymemory.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    // 다가오는 이벤트 개수
    private int upcomingEventsCount;

    // 미구매 선물 개수
    private int unpurchasedGiftsCount;

    // 최근 리마인더 발송 현황
    private RecentReminderStatus recentReminderStatus;

    // 이번 달 이벤트 개수
    private int thisMonthEventsCount;

    // 다가오는 이벤트 목록 (최대 5개)
    private List<EventDto.Response> upcomingEvents;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentReminderStatus {
        // 최근 7일간 발송 성공 건수
        private int sentCount;

        // 최근 7일간 발송 실패 건수
        private int failedCount;

        // 마지막 발송 시간
        private String lastSentAt;
    }
}
