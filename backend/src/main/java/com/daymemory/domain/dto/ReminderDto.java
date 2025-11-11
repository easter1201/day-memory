package com.daymemory.domain.dto;

import com.daymemory.domain.entity.ReminderLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReminderDto {

    @Schema(description = "리마인더 로그 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogResponse {
        @Schema(description = "리마인더 로그 ID", example = "1")
        private Long id;

        @Schema(description = "이벤트 ID", example = "1")
        private Long eventId;

        @Schema(description = "이벤트 제목", example = "엄마 생신")
        private String eventTitle;

        @Schema(description = "이벤트 전 며칠", example = "7")
        private Integer daysBeforeEvent;

        @Schema(description = "발송 시각", example = "2025-11-11T10:30:00")
        private LocalDateTime sentAt;

        @Schema(description = "발송 상태 (SENT, FAILED)", example = "SENT")
        private ReminderLog.ReminderStatus status;

        public static LogResponse from(ReminderLog reminderLog) {
            return LogResponse.builder()
                    .id(reminderLog.getId())
                    .eventId(reminderLog.getEvent().getId())
                    .eventTitle(reminderLog.getEvent().getTitle())
                    .daysBeforeEvent(reminderLog.getDaysBeforeEvent())
                    .sentAt(reminderLog.getSentAt())
                    .status(reminderLog.getStatus())
                    .build();
        }
    }

    @Schema(description = "리마인더 재전송 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetryRequest {
        @Schema(description = "재전송할 리마인더 로그 ID", example = "1")
        private Long reminderLogId;
    }

    @Schema(description = "리마인더 재전송 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetryResponse {
        @Schema(description = "재전송 성공 여부", example = "true")
        private boolean success;

        @Schema(description = "응답 메시지", example = "리마인더가 성공적으로 재전송되었습니다.")
        private String message;

        @Schema(description = "재전송된 리마인더 로그 정보")
        private LogResponse reminderLog;
    }
}
