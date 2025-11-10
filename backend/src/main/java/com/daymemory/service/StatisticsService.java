package com.daymemory.service;

import com.daymemory.domain.dto.StatisticsDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.domain.entity.ReminderLog;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StatisticsService {

    private final EventRepository eventRepository;
    private final GiftItemRepository giftItemRepository;
    private final ReminderLogRepository reminderLogRepository;

    /**
     * 월별 이벤트 통계
     */
    public StatisticsDto.EventStatistics getEventStatistics(Long userId, int year) {
        List<Event> events = eventRepository.findByUserIdAndIsActiveTrue(userId);

        // 해당 연도의 이벤트만 필터링
        List<Event> yearEvents = events.stream()
                .filter(event -> event.getEventDate().getYear() == year)
                .collect(Collectors.toList());

        // 월별 이벤트 수 집계
        Map<String, Integer> monthlyCount = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            String monthKey = String.format("%d-%02d", year, month);
            int finalMonth = month;
            int count = (int) yearEvents.stream()
                    .filter(e -> e.getEventDate().getMonthValue() == finalMonth)
                    .count();
            monthlyCount.put(monthKey, count);
        }

        // 이벤트 타입별 분포
        Map<Event.EventType, Integer> typeDistribution = yearEvents.stream()
                .collect(Collectors.groupingBy(
                        Event::getEventType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        return StatisticsDto.EventStatistics.builder()
                .monthlyEventCount(monthlyCount)
                .eventTypeDistribution(typeDistribution)
                .totalEvents(yearEvents.size())
                .build();
    }

    /**
     * 선물 구매 통계
     */
    public StatisticsDto.GiftStatistics getGiftStatistics(Long userId) {
        List<GiftItem> allGifts = giftItemRepository.findByUserId(userId);

        // 구매한 선물만 필터링
        List<GiftItem> purchasedGifts = allGifts.stream()
                .filter(GiftItem::getIsPurchased)
                .collect(Collectors.toList());

        // 카테고리별 구매 통계
        Map<GiftItem.GiftCategory, Integer> categoryDistribution = purchasedGifts.stream()
                .collect(Collectors.groupingBy(
                        GiftItem::getCategory,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // 총 지출 금액 계산
        int totalSpent = purchasedGifts.stream()
                .filter(gift -> gift.getPrice() != null)
                .mapToInt(GiftItem::getPrice)
                .sum();

        // 평균 선물 가격
        int averagePrice = purchasedGifts.isEmpty() ? 0 :
                (int) purchasedGifts.stream()
                        .filter(gift -> gift.getPrice() != null)
                        .mapToInt(GiftItem::getPrice)
                        .average()
                        .orElse(0);

        return StatisticsDto.GiftStatistics.builder()
                .totalPurchasedGifts(purchasedGifts.size())
                .categoryDistribution(categoryDistribution)
                .totalSpentAmount(totalSpent)
                .averageGiftPrice(averagePrice)
                .build();
    }

    /**
     * 리마인더 발송 통계
     */
    public StatisticsDto.ReminderStatistics getReminderStatistics(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();

        List<ReminderLog> logs = reminderLogRepository.findRemindersBetweenDates(startDate, endDate);

        // 성공/실패 건수
        long sentCount = logs.stream()
                .filter(log -> log.getStatus() == ReminderLog.ReminderStatus.SENT)
                .count();

        long failedCount = logs.stream()
                .filter(log -> log.getStatus() == ReminderLog.ReminderStatus.FAILED)
                .count();

        // 성공률 계산
        double successRate = logs.isEmpty() ? 0.0 :
                (double) sentCount / logs.size() * 100;

        // 일별 통계
        Map<String, StatisticsDto.DailyReminderStats> dailyStats = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateKey = date.format(formatter);

            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(23, 59, 59);

            int daySent = (int) logs.stream()
                    .filter(log -> !log.getSentAt().isBefore(dayStart) && !log.getSentAt().isAfter(dayEnd))
                    .filter(log -> log.getStatus() == ReminderLog.ReminderStatus.SENT)
                    .count();

            int dayFailed = (int) logs.stream()
                    .filter(log -> !log.getSentAt().isBefore(dayStart) && !log.getSentAt().isAfter(dayEnd))
                    .filter(log -> log.getStatus() == ReminderLog.ReminderStatus.FAILED)
                    .count();

            dailyStats.put(dateKey, StatisticsDto.DailyReminderStats.builder()
                    .date(dateKey)
                    .sentCount(daySent)
                    .failedCount(dayFailed)
                    .build());
        }

        return StatisticsDto.ReminderStatistics.builder()
                .sentCount((int) sentCount)
                .failedCount((int) failedCount)
                .successRate(Math.round(successRate * 10) / 10.0)
                .dailyStats(dailyStats)
                .build();
    }

    /**
     * 캘린더 뷰 데이터 조회
     */
    public List<StatisticsDto.CalendarEvent> getCalendarEvents(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Event> events = eventRepository.findUpcomingEvents(userId, startDate, endDate);

        LocalDate today = LocalDate.now();

        return events.stream()
                .map(event -> {
                    int daysRemaining = (int) ChronoUnit.DAYS.between(today, event.getEventDate());
                    return StatisticsDto.CalendarEvent.builder()
                            .eventId(event.getId())
                            .title(event.getTitle())
                            .date(event.getEventDate())
                            .type(event.getEventType())
                            .isTracking(event.getIsTracking())
                            .daysRemaining(daysRemaining)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
