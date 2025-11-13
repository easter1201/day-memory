package com.daymemory.service;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringEventService {

    private final EventRepository eventRepository;

    /**
     * 매일 자정에 실행되어 반복 이벤트를 자동 생성
     * 이벤트 날짜가 지났고, isRecurring이 true인 이벤트를 다음 해로 복사
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void processRecurringEvents() {
        log.info("Starting recurring events processing...");

        LocalDate today = LocalDate.now();

        // 모든 활성화된 반복 이벤트 조회
        List<Event> allRecurringEvents = findAllRecurringEvents();

        int createdCount = 0;
        for (Event event : allRecurringEvents) {
            // 이벤트 날짜가 오늘보다 이전이면 다음 해로 복사
            if (event.getEventDate().isBefore(today)) {
                createNextYearEvent(event);
                createdCount++;
            }
        }

        log.info("Recurring events processing completed. Created {} events", createdCount);
    }

    /**
     * 모든 활성화된 반복 이벤트 조회
     */
    private List<Event> findAllRecurringEvents() {
        // 모든 이벤트 중 반복 이벤트만 필터링
        List<Event> allEvents = eventRepository.findAll();
        List<Event> recurringEvents = new ArrayList<>();

        for (Event event : allEvents) {
            if (Boolean.TRUE.equals(event.getIsRecurring()) && Boolean.TRUE.equals(event.getIsActive())) {
                recurringEvents.add(event);
            }
        }

        return recurringEvents;
    }

    /**
     * 반복 이벤트를 다음 해로 복사
     */
    private void createNextYearEvent(Event originalEvent) {
        LocalDate originalDate = originalEvent.getEventDate();
        LocalDate nextYearDate;

        // 윤년 2월 29일 처리: 다음 해가 윤년이 아니면 2월 28일로 조정
        if (originalDate.getMonthValue() == 2 && originalDate.getDayOfMonth() == 29) {
            int nextYear = originalDate.getYear() + 1;
            // 다음 해가 윤년인지 확인
            if (LocalDate.of(nextYear, 1, 1).isLeapYear()) {
                nextYearDate = LocalDate.of(nextYear, 2, 29);
            } else {
                nextYearDate = LocalDate.of(nextYear, 2, 28);
            }
        } else {
            nextYearDate = originalDate.plusYears(1);
        }

        // 다음 해 이벤트가 이미 존재하는지 확인
        if (isEventAlreadyExists(originalEvent, nextYearDate)) {
            log.debug("Event already exists for next year: {}", originalEvent.getTitle());
            return;
        }

        // 새로운 이벤트 생성 (원본 이벤트 복사)
        // 설명(description)은 제외하고, 대상자명과 관계는 포함
        Event newEvent = Event.builder()
                .user(originalEvent.getUser())
                .title(originalEvent.getTitle())
                .recipientName(originalEvent.getRecipientName())
                .relationship(originalEvent.getRelationship())
                .eventDate(nextYearDate)
                .eventType(originalEvent.getEventType())
                .isRecurring(true)
                .isTracking(originalEvent.getIsTracking())
                .build();

        // 리마인더도 복사
        for (EventReminder originalReminder : originalEvent.getReminders()) {
            EventReminder newReminder = EventReminder.builder()
                    .event(newEvent)
                    .daysBeforeEvent(originalReminder.getDaysBeforeEvent())
                    .isActive(originalReminder.getIsActive())
                    .build();
            newEvent.addReminder(newReminder);
        }

        eventRepository.save(newEvent);

        // 원본 이벤트 비활성화 (선택사항 - 원하면 활성화 상태 유지 가능)
        originalEvent.deactivate();

        log.info("Created recurring event for next year: {} -> {}",
                originalEvent.getEventDate(), nextYearDate);
    }

    /**
     * 같은 사용자, 같은 제목, 같은 날짜의 이벤트가 이미 존재하는지 확인
     */
    private boolean isEventAlreadyExists(Event originalEvent, LocalDate targetDate) {
        List<Event> userEvents = eventRepository.findByUserIdAndIsActiveTrue(originalEvent.getUser().getId());

        return userEvents.stream()
                .anyMatch(e -> e.getTitle().equals(originalEvent.getTitle())
                        && e.getEventDate().equals(targetDate));
    }

    /**
     * 수동으로 반복 이벤트 생성 (테스트용)
     */
    @Transactional
    public void createRecurringEventManually(Long eventId) {
        Event event = eventRepository.findByIdWithUserAndReminders(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        if (!Boolean.TRUE.equals(event.getIsRecurring())) {
            throw new CustomException(ErrorCode.EVENT_NOT_RECURRING);
        }

        createNextYearEvent(event);
    }
}
