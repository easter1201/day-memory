package com.daymemory.service;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.daymemory.domain.entity.ReminderLog;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.ReminderLogRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final EventRepository eventRepository;
    private final ReminderLogRepository reminderLogRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시 실행
    @Transactional
    public void sendDailyReminders() {
        log.info("Starting daily reminder check...");

        LocalDate today = LocalDate.now();

        // 가능한 모든 리마인더 일수 범위 확인 (1일부터 365일까지)
        // 실제로는 활성화된 리마인더만 확인하기 위해 추적 중인 이벤트를 조회
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today.plusDays(365);

        List<Event> trackingEvents = eventRepository.findTrackingEventsBetweenDates(startDate, endDate);

        // 날짜별로 그룹화
        Map<LocalDate, List<Event>> eventsByDate = trackingEvents.stream()
                .collect(Collectors.groupingBy(Event::getEventDate));

        // 각 날짜에 대해 리마인더 확인 및 발송
        for (Map.Entry<LocalDate, List<Event>> entry : eventsByDate.entrySet()) {
            LocalDate eventDate = entry.getKey();
            List<Event> events = entry.getValue();

            int daysUntilEvent = (int) java.time.temporal.ChronoUnit.DAYS.between(today, eventDate);

            for (Event event : events) {
                checkAndSendRemindersForEvent(event, daysUntilEvent);
            }
        }

        log.info("Daily reminder check completed.");
    }

    private void checkAndSendRemindersForEvent(Event event, int daysUntilEvent) {
        // 이 이벤트의 활성화된 리마인더 중 오늘 발송해야 할 것들 찾기
        List<EventReminder> remindersToSend = event.getReminders().stream()
                .filter(EventReminder::getIsActive)
                .filter(reminder -> reminder.getDaysBeforeEvent().equals(daysUntilEvent))
                .collect(Collectors.toList());

        for (EventReminder reminder : remindersToSend) {
            sendReminder(event, reminder.getDaysBeforeEvent());
        }
    }

    private void sendReminder(Event event, int daysBeforeEvent) {
        try {
            // 이미 최근에 보냈는지 확인 (24시간 이내)
            LocalDateTime checkDate = LocalDateTime.now().minusHours(24);
            Optional<ReminderLog> recentReminder = reminderLogRepository.findRecentReminder(
                    event.getId(), daysBeforeEvent, checkDate
            );

            if (recentReminder.isPresent()) {
                log.debug("Reminder already sent for event: {} ({} days before)",
                        event.getId(), daysBeforeEvent);
                return;
            }

            // 이메일 발송
            String subject = String.format("[Day Memory] '%s' %d일 전 알림",
                    event.getTitle(), daysBeforeEvent);
            String content = emailService.buildReminderEmailContent(event.getTitle(), daysBeforeEvent);
            emailService.sendReminderEmail(event.getUser().getEmail(), subject, content);

            // 로그 저장
            ReminderLog reminderLog = ReminderLog.builder()
                    .event(event)
                    .daysBeforeEvent(daysBeforeEvent)
                    .sentAt(LocalDateTime.now())
                    .status(ReminderLog.ReminderStatus.SENT)
                    .build();
            reminderLogRepository.save(reminderLog);

            log.info("Reminder sent for event: {} ({} days before)",
                    event.getTitle(), daysBeforeEvent);

        } catch (Exception e) {
            log.error("Failed to send reminder for event: {} ({} days before)",
                    event.getId(), daysBeforeEvent, e);

            // 실패 로그 저장
            ReminderLog failedLog = ReminderLog.builder()
                    .event(event)
                    .daysBeforeEvent(daysBeforeEvent)
                    .sentAt(LocalDateTime.now())
                    .status(ReminderLog.ReminderStatus.FAILED)
                    .build();
            reminderLogRepository.save(failedLog);
        }
    }

    /**
     * 특정 이벤트에 대한 즉시 리마인더 발송 (테스트용)
     */
    @Transactional
    public void sendImmediateReminder(Long eventId) {
        Event event = eventRepository.findByIdWithUserAndReminders(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        LocalDate today = LocalDate.now();
        int daysUntilEvent = (int) java.time.temporal.ChronoUnit.DAYS.between(today, event.getEventDate());

        if (daysUntilEvent < 0) {
            log.warn("Event {} has already passed", eventId);
            return;
        }

        // 가장 가까운 리마인더 찾기
        Optional<EventReminder> nearestReminder = event.getReminders().stream()
                .filter(EventReminder::getIsActive)
                .filter(r -> r.getDaysBeforeEvent() <= daysUntilEvent)
                .min(Comparator.comparing(EventReminder::getDaysBeforeEvent));

        nearestReminder.ifPresent(reminder ->
                sendReminder(event, reminder.getDaysBeforeEvent())
        );
    }
}
