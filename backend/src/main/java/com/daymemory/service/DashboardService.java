package com.daymemory.service;

import com.daymemory.domain.dto.DashboardDto;
import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.domain.entity.ReminderLog;
import com.daymemory.domain.repository.EventReminderRepository;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.GiftItemRepository;
import com.daymemory.domain.repository.ReminderLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DashboardService {

    private final EventRepository eventRepository;
    private final GiftItemRepository giftItemRepository;
    private final ReminderLogRepository reminderLogRepository;
    private final EventReminderRepository eventReminderRepository;

    /**
     * 대시보드 요약 정보 조회
     */
    public DashboardDto getDashboardSummary(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate next90Days = today.plusDays(90);

        // 다가오는 이벤트 조회 (90일 이내)
        List<Event> upcomingEventsList = eventRepository.findUpcomingEvents(userId, today, next90Days);

        // 미구매 선물 조회
        List<GiftItem> unpurchasedGifts = giftItemRepository.findByUserIdAndIsPurchasedFalse(userId);

        // 최근 리마인더 발송 현황 (최근 7일)
        DashboardDto.RecentReminderStatus reminderStatus = getRecentReminderStatus();

        // 이번 달 이벤트 조회
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        List<Event> thisMonthEvents = eventRepository.findUpcomingEvents(userId, monthStart, monthEnd);

        // 모든 다가오는 이벤트 반환 (프론트엔드에서 스크롤 처리)
        List<EventDto.Response> upcomingEventsDto = upcomingEventsList.stream()
                .map(EventDto.Response::from)
                .collect(Collectors.toList());

        // 오늘 발송 예정인 리마인더 조회
        List<DashboardDto.TodayReminderDto> todayReminders = getTodayReminders(userId, today);

        return DashboardDto.builder()
                .upcomingEventsCount(upcomingEventsList.size())
                .unpurchasedGiftsCount(unpurchasedGifts.size())
                .recentReminderStatus(reminderStatus)
                .thisMonthEventsCount(thisMonthEvents.size())
                .upcomingEvents(upcomingEventsDto)
                .todayReminders(todayReminders)
                .build();
    }

    /**
     * 최근 리마인더 발송 현황 (최근 7일)
     */
    private DashboardDto.RecentReminderStatus getRecentReminderStatus() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();

        List<ReminderLog> recentLogs = reminderLogRepository.findRemindersBetweenDates(sevenDaysAgo, now);

        long sentCount = recentLogs.stream()
                .filter(log -> log.getStatus() == ReminderLog.ReminderStatus.SENT)
                .count();

        long failedCount = recentLogs.stream()
                .filter(log -> log.getStatus() == ReminderLog.ReminderStatus.FAILED)
                .count();

        String lastSentAt = recentLogs.isEmpty()
                ? "없음"
                : recentLogs.get(0).getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        return DashboardDto.RecentReminderStatus.builder()
                .sentCount((int) sentCount)
                .failedCount((int) failedCount)
                .lastSentAt(lastSentAt)
                .build();
    }

    /**
     * 오늘 발송 예정인 리마인더 조회
     */
    private List<DashboardDto.TodayReminderDto> getTodayReminders(Long userId, LocalDate today) {
        log.info("getTodayReminders - userId: {}, today: {}", userId, today);

        // 모든 활성 리마인더 조회 후 Java에서 필터링
        List<EventReminder> allReminders = eventReminderRepository.findActiveRemindersByUserId(userId);

        log.info("getTodayReminders - found {} total active reminders", allReminders.size());

        List<DashboardDto.TodayReminderDto> result = allReminders.stream()
                .filter(reminder -> {
                    Event event = reminder.getEvent();
                    long daysUntilEvent = ChronoUnit.DAYS.between(today, event.getEventDate());
                    // 오늘 발송해야 하는 리마인더 필터링
                    return daysUntilEvent == reminder.getDaysBeforeEvent();
                })
                .map(reminder -> {
                    Event event = reminder.getEvent();
                    long daysUntilEvent = ChronoUnit.DAYS.between(today, event.getEventDate());

                    log.info("  - Event: id={}, title={}, eventDate={}, daysBeforeEvent={}, daysUntilEvent={}",
                            event.getId(), event.getTitle(), event.getEventDate(),
                            reminder.getDaysBeforeEvent(), daysUntilEvent);

                    return DashboardDto.TodayReminderDto.builder()
                            .eventId(event.getId())
                            .eventTitle(event.getTitle())
                            .recipientName(event.getRecipientName())
                            .eventDate(event.getEventDate().toString())
                            .daysUntilEvent((int) daysUntilEvent)
                            .build();
                })
                .collect(Collectors.toList());

        log.info("getTodayReminders - returning {} reminder DTOs", result.size());

        return result;
    }
}
