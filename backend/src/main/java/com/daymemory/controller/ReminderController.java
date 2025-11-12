package com.daymemory.controller;

import com.daymemory.domain.dto.ReminderDto;
import com.daymemory.domain.entity.ReminderLog;
import com.daymemory.service.ReminderService;
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
import java.util.stream.Collectors;

@Tag(name = "Reminder", description = "리마인더 관리 API - 이벤트 알림 발송 및 이력을 관리합니다.")
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    /**
     * 리마인더 발송 이력 조회
     * GET /api/reminders/logs - 최근 30일간 모든 리마인더 로그
     * GET /api/reminders/logs?eventId={eventId} - 특정 이벤트의 리마인더 로그
     */
    @Operation(summary = "리마인더 발송 이력 조회", description = "최근 30일간의 리마인더 발송 이력을 조회합니다. eventId를 지정하면 특정 이벤트의 로그만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이력 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReminderDto.LogResponse.class)))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음 (eventId 지정 시)",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/logs")
    public ResponseEntity<List<ReminderDto.LogResponse>> getReminderLogs(
            @RequestParam(required = false) Long eventId) {
        List<ReminderLog> logs = reminderService.getReminderLogs(eventId);
        List<ReminderDto.LogResponse> responses = logs.stream()
                .map(ReminderDto.LogResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 실패한 리마인더 재발송
     * POST /api/reminders/retry/{reminderLogId}
     */
    @Operation(summary = "실패한 리마인더 재발송", description = "발송에 실패한 리마인더를 다시 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발송 요청 처리 완료",
                    content = @Content(schema = @Schema(implementation = ReminderDto.RetryResponse.class))),
            @ApiResponse(responseCode = "404", description = "리마인더 로그를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/retry/{reminderLogId}")
    public ResponseEntity<ReminderDto.RetryResponse> retryFailedReminder(
            @PathVariable Long reminderLogId) {
        boolean success = reminderService.retryFailedReminder(reminderLogId);

        ReminderDto.RetryResponse response = ReminderDto.RetryResponse.builder()
                .success(success)
                .message(success ? "리마인더가 재발송되었습니다." : "리마인더 재발송에 실패했습니다.")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 즉시 리마인더 발송 (테스트용)
     * POST /api/reminders/immediate/{eventId}
     */
    @Operation(summary = "즉시 리마인더 발송", description = "테스트 목적으로 특정 이벤트의 리마인더를 즉시 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리마인더 발송 성공"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/immediate/{eventId}")
    public ResponseEntity<String> sendImmediateReminder(@PathVariable Long eventId) {
        reminderService.sendImmediateReminder(eventId);
        return ResponseEntity.ok("즉시 리마인더가 발송되었습니다.");
    }
}
