package com.daymemory.domain.dto;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "이벤트 제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        private String title;

        @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
        private String description;

        @NotNull(message = "이벤트 날짜는 필수입니다.")
        @FutureOrPresent(message = "이벤트 날짜는 오늘 이후여야 합니다.")
        private LocalDate eventDate;

        @NotNull(message = "이벤트 타입은 필수입니다.")
        private Event.EventType eventType;

        private Boolean isRecurring;
        private Boolean isTracking;
        private List<@Positive(message = "리마인더 일수는 양수여야 합니다.") Integer> reminderDays;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private LocalDate eventDate;
        private Event.EventType eventType;
        private Boolean isRecurring;
        private Boolean isActive;
        private Boolean isTracking;
        private Long dDay;
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReminderDto {
        private Long id;
        private Integer daysBeforeEvent;
        private Boolean isActive;

        public static ReminderDto from(EventReminder reminder) {
            return ReminderDto.builder()
                    .id(reminder.getId())
                    .daysBeforeEvent(reminder.getDaysBeforeEvent())
                    .isActive(reminder.getIsActive())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateReminderRequest {
        private List<Integer> reminderDays;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToggleTrackingRequest {
        private Boolean isTracking;
    }
}
