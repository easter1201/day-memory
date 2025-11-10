package com.daymemory.controller;

import com.daymemory.domain.dto.ReminderDto;
import com.daymemory.domain.entity.ReminderLog;
import com.daymemory.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
     * 실패한 리마인더 목록 조회
     * GET /api/reminders/failed - 최근 30일간 실패한 리마인더
     */
    @GetMapping("/failed")
    public ResponseEntity<List<ReminderDto.LogResponse>> getFailedReminders() {
        List<ReminderLog> logs = reminderService.getFailedReminders();
        List<ReminderDto.LogResponse> responses = logs.stream()
                .map(ReminderDto.LogResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 실패한 리마인더 재발송
     * POST /api/reminders/retry/{reminderLogId}
     */
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
    @PostMapping("/immediate/{eventId}")
    public ResponseEntity<String> sendImmediateReminder(@PathVariable Long eventId) {
        reminderService.sendImmediateReminder(eventId);
        return ResponseEntity.ok("즉시 리마인더가 발송되었습니다.");
    }
}
