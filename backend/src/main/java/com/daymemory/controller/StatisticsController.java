package com.daymemory.controller;

import com.daymemory.domain.dto.StatisticsDto;
import com.daymemory.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 월별 이벤트 통계
     * GET /api/statistics/events?userId={userId}&year={year}
     */
    @GetMapping("/events")
    public ResponseEntity<StatisticsDto.EventStatistics> getEventStatistics(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "2025") int year) {
        StatisticsDto.EventStatistics statistics = statisticsService.getEventStatistics(userId, year);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 선물 구매 통계
     * GET /api/statistics/gifts?userId={userId}
     */
    @GetMapping("/gifts")
    public ResponseEntity<StatisticsDto.GiftStatistics> getGiftStatistics(@RequestParam Long userId) {
        StatisticsDto.GiftStatistics statistics = statisticsService.getGiftStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 리마인더 발송 통계
     * GET /api/statistics/reminders?days={days}
     */
    @GetMapping("/reminders")
    public ResponseEntity<StatisticsDto.ReminderStatistics> getReminderStatistics(
            @RequestParam(defaultValue = "30") int days) {
        StatisticsDto.ReminderStatistics statistics = statisticsService.getReminderStatistics(days);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 캘린더 뷰 데이터 조회
     * GET /api/calendar?userId={userId}&year={year}&month={month}
     */
    @GetMapping("/calendar")
    public ResponseEntity<List<StatisticsDto.CalendarEvent>> getCalendarEvents(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        List<StatisticsDto.CalendarEvent> events = statisticsService.getCalendarEvents(userId, year, month);
        return ResponseEntity.ok(events);
    }
}
