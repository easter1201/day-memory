package com.daymemory.domain.dto;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class EventDto {

    @Schema(description = "이벤트 생성/수정 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @Schema(description = "이벤트 제목", example = "엄마 생신", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이벤트 제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        private String title;

        @Schema(description = "이벤트 설명", example = "70세 생신 기념")
        @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
        private String description;

        @Schema(description = "이벤트 날짜", example = "2025-12-25", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "이벤트 날짜는 필수입니다.")
        @FutureOrPresent(message = "이벤트 날짜는 오늘 이후여야 합니다.")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate eventDate;

        @Schema(description = "이벤트 타입 (BIRTHDAY, ANNIVERSARY, HOLIDAY, GIFT)", example = "BIRTHDAY", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "이벤트 타입은 필수입니다.")
        private Event.EventType eventType;

        @Schema(description = "매년 반복 여부", example = "true")
        private Boolean isRecurring;

        @Schema(description = "트래킹 활성화 여부", example = "true")
        private Boolean isTracking;

        @Schema(description = "리마인더 설정 (이벤트 전 며칠)", example = "[1, 3, 7]")
        private List<@Positive(message = "리마인더 일수는 양수여야 합니다.") Integer> reminderDays;
    }

    @Schema(description = "이벤트 응답")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @Schema(description = "이벤트 ID", example = "1")
        private Long id;

        @Schema(description = "이벤트 제목", example = "엄마 생신")
        private String title;

        @Schema(description = "이벤트 설명", example = "70세 생신 기념")
        private String description;

        @Schema(description = "이벤트 날짜", example = "2025-12-25")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate eventDate;

        @Schema(description = "이벤트 타입", example = "BIRTHDAY")
        private Event.EventType eventType;

        @Schema(description = "매년 반복 여부", example = "true")
        private Boolean isRecurring;

        @Schema(description = "활성화 상태", example = "true")
        private Boolean isActive;

        @Schema(description = "트래킹 활성화 여부", example = "true")
        private Boolean isTracking;

        @Schema(description = "D-Day (남은 일수)", example = "45")
        private Long dDay;

        @Schema(description = "설정된 리마인더 목록")
        private List<ReminderDto> reminders;

        public static Response from(Event event) {
            long dDay = ChronoUnit.DAYS.between(LocalDate.now(), event.getEventDate());

            List<ReminderDto> reminderDtos = event.getReminders().stream()
                    .map(ReminderDto::from)
                    .collect(Collectors.toList());

            return Response.builder()
                    .id(event.getId())
                    .title(event.getTitle())
                    .description(event.getDescription())
                    .eventDate(event.getEventDate())
                    .eventType(event.getEventType())
                    .isRecurring(event.getIsRecurring())
                    .isActive(event.getIsActive())
                    .isTracking(event.getIsTracking())
                    .dDay(dDay)
                    .reminders(reminderDtos)
                    .build();
        }
    }

    @Schema(description = "이벤트 리마인더 정보")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReminderDto {
        @Schema(description = "리마인더 ID", example = "1")
        private Long id;

        @Schema(description = "이벤트 전 며칠", example = "7")
        private Integer daysBeforeEvent;

        @Schema(description = "활성화 상태", example = "true")
        private Boolean isActive;

        public static ReminderDto from(EventReminder reminder) {
            return ReminderDto.builder()
                    .id(reminder.getId())
                    .daysBeforeEvent(reminder.getDaysBeforeEvent())
                    .isActive(reminder.getIsActive())
                    .build();
        }
    }

    @Schema(description = "리마인더 업데이트 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateReminderRequest {
        @Schema(description = "리마인더 일수 목록 (이벤트 전 며칠)", example = "[1, 3, 7]")
        private List<Integer> reminderDays;
    }

    @Schema(description = "트래킹 활성화/비활성화 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToggleTrackingRequest {
        @Schema(description = "트래킹 활성화 여부", example = "true")
        private Boolean isTracking;
    }
}
