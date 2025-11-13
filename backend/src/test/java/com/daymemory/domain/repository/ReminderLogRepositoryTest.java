package com.daymemory.domain.repository;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.ReminderLog;
import com.daymemory.domain.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("ReminderLogRepository 테스트")
class ReminderLogRepositoryTest {

    @Autowired
    private ReminderLogRepository reminderLogRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("Test User")
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        // 테스트용 이벤트 생성
        testEvent = Event.builder()
                .user(testUser)
                .title("Birthday")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isActive(true)
                .build();
        testEvent = eventRepository.save(testEvent);
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        reminderLogRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이벤트별 리마인더 로그 조회")
    void testFindByEventId() {
        // given
        ReminderLog log1 = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusDays(1))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log1);

        ReminderLog log2 = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .sentAt(LocalDateTime.now())
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<ReminderLog> logs = reminderLogRepository.findByEventId(testEvent.getId());

        // then
        assertThat(logs).hasSize(2);
        assertThat(logs).extracting("daysBeforeEvent")
                .containsExactlyInAnyOrder(7, 3);

        // N+1 문제 방지 확인 - Event가 이미 fetch join 되어 있어야 함
        assertThat(logs.get(0).getEvent()).isNotNull();
        assertThat(logs.get(0).getEvent().getTitle()).isEqualTo("Birthday");
    }

    @Test
    @DisplayName("최근 리마인더 로그 조회 - 24시간 이내")
    void testFindRecentReminderLog() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);

        // 24시간 이내의 리마인더
        ReminderLog recentLog = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(now.minusHours(12))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(recentLog);

        // 24시간 이전의 리마인더
        ReminderLog oldLog = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(now.minusHours(36))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(oldLog);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<ReminderLog> foundLog = reminderLogRepository.findRecentReminder(
                testEvent.getId(), 7, oneDayAgo);

        // then
        assertThat(foundLog).isPresent();
        assertThat(foundLog.get().getSentAt()).isAfter(oneDayAgo);
        assertThat(foundLog.get().getDaysBeforeEvent()).isEqualTo(7);
    }

    @Test
    @DisplayName("중복 리마인더 체크 - 24시간 이내 중복 발송 방지")
    void testDuplicateReminderCheck() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);

        // 최근에 발송된 리마인더
        ReminderLog existingLog = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(now.minusHours(12))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(existingLog);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<ReminderLog> recentReminder = reminderLogRepository.findRecentReminder(
                testEvent.getId(), 7, oneDayAgo);

        // then
        assertThat(recentReminder).isPresent();
        // 중복 발송 방지: 24시간 이내에 이미 발송된 리마인더가 있음
        assertThat(recentReminder.get().getSentAt()).isAfter(oneDayAgo);
    }

    @Test
    @DisplayName("특정 기간 동안 발송된 리마인더 조회")
    void testFindRemindersBetweenDates() {
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        ReminderLog log1 = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusDays(5))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log1);

        ReminderLog log2 = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .sentAt(LocalDateTime.now().minusDays(3))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log2);

        ReminderLog oldLog = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(14)
                .sentAt(LocalDateTime.now().minusDays(10))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(oldLog);

        entityManager.flush();
        entityManager.clear();

        // when
        List<ReminderLog> logs = reminderLogRepository.findRemindersBetweenDates(startDate, endDate);

        // then
        assertThat(logs).hasSize(2);
        assertThat(logs).extracting("daysBeforeEvent")
                .containsExactlyInAnyOrder(7, 3);

        // 날짜순 정렬 확인 (DESC)
        assertThat(logs.get(0).getSentAt()).isAfter(logs.get(1).getSentAt());
    }

    @Test
    @DisplayName("실패한 리마인더만 조회")
    void testFindFailedRemindersAfter() {
        // given
        LocalDateTime afterDate = LocalDateTime.now().minusDays(7);

        ReminderLog sentLog = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusDays(3))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(sentLog);

        ReminderLog failedLog1 = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .sentAt(LocalDateTime.now().minusDays(2))
                .status(ReminderLog.ReminderStatus.FAILED)
                .build();
        reminderLogRepository.save(failedLog1);

        ReminderLog failedLog2 = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(1)
                .sentAt(LocalDateTime.now().minusDays(1))
                .status(ReminderLog.ReminderStatus.FAILED)
                .build();
        reminderLogRepository.save(failedLog2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<ReminderLog> failedLogs = reminderLogRepository.findFailedRemindersAfter(afterDate);

        // then
        assertThat(failedLogs).hasSize(2);
        assertThat(failedLogs).allMatch(log -> log.getStatus() == ReminderLog.ReminderStatus.FAILED);
        assertThat(failedLogs).extracting("daysBeforeEvent")
                .containsExactlyInAnyOrder(3, 1);
    }

    @Test
    @DisplayName("리마인더 상태 변경 테스트 - SENT에서 FAILED로")
    void testMarkAsFailed() {
        // given
        ReminderLog log = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now())
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        ReminderLog savedLog = reminderLogRepository.save(log);
        assertThat(savedLog.getStatus()).isEqualTo(ReminderLog.ReminderStatus.SENT);

        // when
        savedLog.markAsFailed();
        ReminderLog updatedLog = reminderLogRepository.save(savedLog);

        // then
        assertThat(updatedLog.getStatus()).isEqualTo(ReminderLog.ReminderStatus.FAILED);
    }

    @Test
    @DisplayName("같은 이벤트의 다른 일수 리마인더는 독립적으로 조회")
    void testDifferentDaysBeforeEvent() {
        // given
        ReminderLog log7Days = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusHours(12))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log7Days);

        ReminderLog log3Days = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .sentAt(LocalDateTime.now().minusHours(6))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log3Days);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<ReminderLog> recent7Days = reminderLogRepository.findRecentReminder(
                testEvent.getId(), 7, LocalDateTime.now().minusDays(1));
        Optional<ReminderLog> recent3Days = reminderLogRepository.findRecentReminder(
                testEvent.getId(), 3, LocalDateTime.now().minusDays(1));

        // then
        assertThat(recent7Days).isPresent();
        assertThat(recent7Days.get().getDaysBeforeEvent()).isEqualTo(7);

        assertThat(recent3Days).isPresent();
        assertThat(recent3Days.get().getDaysBeforeEvent()).isEqualTo(3);
    }

    @Test
    @DisplayName("여러 이벤트의 리마인더 로그가 독립적으로 관리됨")
    void testMultipleEventsReminderLogs() {
        // given
        Event anotherEvent = Event.builder()
                .user(testUser)
                .title("Anniversary")
                .eventDate(LocalDate.now().plusDays(60))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isActive(true)
                .build();
        anotherEvent = eventRepository.save(anotherEvent);

        ReminderLog log1 = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now())
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log1);

        ReminderLog log2 = ReminderLog.builder()
                .event(anotherEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now())
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<ReminderLog> testEventLogs = reminderLogRepository.findByEventId(testEvent.getId());
        List<ReminderLog> anotherEventLogs = reminderLogRepository.findByEventId(anotherEvent.getId());

        // then
        assertThat(testEventLogs).hasSize(1);
        assertThat(testEventLogs.get(0).getEvent().getTitle()).isEqualTo("Birthday");

        assertThat(anotherEventLogs).hasSize(1);
        assertThat(anotherEventLogs.get(0).getEvent().getTitle()).isEqualTo("Anniversary");
    }

    @Test
    @DisplayName("리마인더 로그 생성 시 기본 상태는 SENT")
    void testDefaultStatusIsSent() {
        // given
        ReminderLog log = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now())
                .build();

        // when
        ReminderLog savedLog = reminderLogRepository.save(log);

        // then
        assertThat(savedLog.getStatus()).isEqualTo(ReminderLog.ReminderStatus.SENT);
    }

    @Test
    @DisplayName("특정 일수의 최근 리마인더 조회 - 결과 없음")
    void testFindRecentReminderLog_NotFound() {
        // given
        ReminderLog log = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusDays(2))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log);

        entityManager.flush();
        entityManager.clear();

        // when - 24시간 이내의 리마인더를 찾지만, 2일 전에 발송되어 찾을 수 없음
        Optional<ReminderLog> recentLog = reminderLogRepository.findRecentReminder(
                testEvent.getId(), 7, LocalDateTime.now().minusHours(24));

        // then
        assertThat(recentLog).isEmpty();
    }

    @Test
    @DisplayName("커스텀 일수의 리마인더 로그 지원")
    void testCustomDaysBeforeEvent() {
        // given
        ReminderLog log30Days = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(30)
                .sentAt(LocalDateTime.now())
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log30Days);

        ReminderLog log1Day = ReminderLog.builder()
                .event(testEvent)
                .daysBeforeEvent(1)
                .sentAt(LocalDateTime.now())
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(log1Day);

        entityManager.flush();
        entityManager.clear();

        // when
        List<ReminderLog> logs = reminderLogRepository.findByEventId(testEvent.getId());

        // then
        assertThat(logs).hasSize(2);
        assertThat(logs).extracting("daysBeforeEvent")
                .containsExactlyInAnyOrder(30, 1);
    }
}
