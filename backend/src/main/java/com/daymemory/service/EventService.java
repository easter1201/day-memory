package com.daymemory.service;

import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventDto.Response createEvent(Long userId, EventDto.Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Event event = Event.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .recipientName(request.getRecipientName())
                .relationship(request.getRelationship())
                .eventDate(request.getEventDate())
                .eventType(request.getEventType())
                .isRecurring(request.getIsRecurring())
                .isTracking(request.getIsTracking() != null ? request.getIsTracking() : true)
                .build();

        // 리마인더 설정 (기본값: 30일, 7일, 1일)
        List<Integer> reminderDays = request.getReminderDays() != null && !request.getReminderDays().isEmpty()
                ? request.getReminderDays()
                : List.of(30, 7, 1);

        for (Integer days : reminderDays) {
            EventReminder reminder = EventReminder.builder()
                    .event(event)
                    .daysBeforeEvent(days)
                    .isActive(true)
                    .build();
            event.addReminder(reminder);
        }

        Event savedEvent = eventRepository.save(event);
        return EventDto.Response.from(savedEvent);
    }

    public List<EventDto.Response> getEventsByUser(Long userId) {
        List<Event> events = eventRepository.findByUserIdAndIsActiveTrue(userId);
        return events.stream()
                .map(EventDto.Response::from)
                .collect(Collectors.toList());
    }

    public List<EventDto.Response> getEventsByUserAndType(Long userId, Event.EventType eventType) {
        List<Event> events = eventRepository.findByUserIdAndEventTypeAndIsActiveTrue(userId, eventType);
        return events.stream()
                .map(EventDto.Response::from)
                .collect(Collectors.toList());
    }

    public EventDto.Response getEvent(Long eventId) {
        Event event = eventRepository.findByIdWithUserAndReminders(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        return EventDto.Response.from(event);
    }

    @Transactional
    public EventDto.Response updateEvent(Long eventId, EventDto.Request request) {
        Event event = eventRepository.findByIdWithUserAndReminders(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        event.update(
                request.getTitle(),
                request.getDescription(),
                request.getRecipientName(),
                request.getRelationship(),
                request.getEventDate(),
                request.getEventType(),
                request.getIsRecurring()
        );

        // 리마인더 업데이트
        if (request.getReminderDays() != null) {
            updateEventReminders(event, request.getReminderDays());
        }

        // 추적 상태 업데이트
        if (request.getIsTracking() != null) {
            event.setTracking(request.getIsTracking());
        }

        return EventDto.Response.from(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        event.deactivate();
    }

    public List<EventDto.Response> getUpcomingEvents(Long userId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<Event> events = eventRepository.findUpcomingEvents(userId, today, endDate);
        return events.stream()
                .map(EventDto.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventDto.Response updateReminders(Long eventId, EventDto.UpdateReminderRequest request) {
        Event event = eventRepository.findByIdWithUserAndReminders(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        updateEventReminders(event, request.getReminderDays());

        return EventDto.Response.from(event);
    }

    @Transactional
    public EventDto.Response toggleTracking(Long eventId, Boolean isTracking) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        event.setTracking(isTracking);

        return EventDto.Response.from(event);
    }

    private void updateEventReminders(Event event, List<Integer> reminderDays) {
        // 기존 리마인더 제거
        event.clearReminders();

        // 새로운 리마인더 추가
        for (Integer days : reminderDays) {
            EventReminder reminder = EventReminder.builder()
                    .event(event)
                    .daysBeforeEvent(days)
                    .isActive(true)
                    .build();
            event.addReminder(reminder);
        }
    }

    public List<Event> getEventsRequiringReminder(LocalDate targetDate) {
        return eventRepository.findEventsByDate(targetDate);
    }
}
