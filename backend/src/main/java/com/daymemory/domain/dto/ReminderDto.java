package com.daymemory.domain.dto;

import com.daymemory.domain.entity.ReminderLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReminderDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogResponse {
        private Long id;
        private Long eventId;
        private String eventTitle;
        private Integer daysBeforeEvent;
        private LocalDateTime sentAt;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetryRequest {
        private Long reminderLogId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetryResponse {
        private boolean success;
        private String message;
        private LogResponse reminderLog;
    }
}
