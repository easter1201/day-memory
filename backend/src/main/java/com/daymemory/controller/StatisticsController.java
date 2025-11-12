package com.daymemory.controller;

import com.daymemory.domain.dto.StatisticsDto;
import com.daymemory.security.SecurityUtils;
import com.daymemory.service.StatisticsService;
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

import java.util.List;

@Tag(name = "Statistics", description = "통계 API - 이벤트, 선물, 리마인더 등의 통계 정보를 제공합니다.")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 월별 이벤트 통계
     * GET /api/statistics/events?userId={userId}&year={year}
     */
    @Operation(summary = "월별 이벤트 통계", description = "특정 연도의 월별 이벤트 통계를 조회합니다. (기본값: 2025년)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 조회 성공",
                    content = @Content(schema = @Schema(implementation = StatisticsDto.EventStatistics.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/events")
    public ResponseEntity<StatisticsDto.EventStatistics> getEventStatistics(
            @RequestParam(defaultValue = "2025") int year) {
        Long userId = SecurityUtils.getCurrentUserId();
        StatisticsDto.EventStatistics statistics = statisticsService.getEventStatistics(userId, year);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 선물 구매 통계
     * GET /api/statistics/gifts?userId={userId}
     */
    @Operation(summary = "선물 구매 통계", description = "사용자의 선물 구매 통계를 조회합니다. (전체/구매완료/미구매, 카테고리별 통계 등)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 조회 성공",
                    content = @Content(schema = @Schema(implementation = StatisticsDto.GiftStatistics.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/gifts")
    public ResponseEntity<StatisticsDto.GiftStatistics> getGiftStatistics() {
        Long userId = SecurityUtils.getCurrentUserId();
        StatisticsDto.GiftStatistics statistics = statisticsService.getGiftStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 리마인더 발송 통계
     * GET /api/statistics/reminders?days={days}
     */
    @Operation(summary = "리마인더 발송 통계", description = "지정된 기간 동안의 리마인더 발송 통계를 조회합니다. (기본값: 30일)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 조회 성공",
                    content = @Content(schema = @Schema(implementation = StatisticsDto.ReminderStatistics.class)))
    })
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
    @Operation(summary = "캘린더 뷰 데이터 조회", description = "특정 연월의 캘린더 이벤트 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "캘린더 데이터 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = StatisticsDto.CalendarEvent.class)))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/calendar")
    public ResponseEntity<List<StatisticsDto.CalendarEvent>> getCalendarEvents(
            @RequestParam int year,
            @RequestParam int month) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<StatisticsDto.CalendarEvent> events = statisticsService.getCalendarEvents(userId, year, month);
        return ResponseEntity.ok(events);
    }
}
