package com.daymemory.integration;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.daymemory.domain.entity.ReminderLog;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.ReminderLogRepository;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.service.EmailService;
import com.daymemory.service.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 스케줄러 통합 테스트
 * - ReminderService의 스케줄 메서드가 정상적으로 동작하는지 확인
 * - 리마인더가 올바른 시점에 발송되는지 확인
 */
@SpringBootTest
@ActiveProfiles("test")
@EnableScheduling
@Transactional
@DisplayName("스케줄러 통합 테스트")
class SchedulerIntegrationTest {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReminderLogRepository reminderLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SpyBean
    private EmailService emailService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("Test User")
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        // EmailService의 실제 메서드가 호출되지 않도록 모킹
        doNothing().when(emailService).sendReminderEmail(anyString(), anyString(), anyString());
        when(emailService.buildReminderEmailContent(anyString(), anyInt(), anyString()))
                .thenReturn("Test reminder email content");
    }

    @Test
    @DisplayName("7일 전 리마인더가 정상적으로 발송됨")
    void testSendDailyReminders_7DaysBefore() {
        // Given: 7일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(7);
        Event event = Event.builder()
                .user(testUser)
                .title("테스트 이벤트")
                .description("7일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .build();

        // 7일 전 리마인더 추가
        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        event.addReminder(reminder);
        reminder = eventRepository.save(event).getReminders().get(0);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 리마인더가 발송되었는지 확인
        verify(emailService, times(1))
                .sendReminderEmail(eq(testUser.getEmail()), contains("7일 전"), anyString());

        // 리마인더 로그가 생성되었는지 확인
        List<ReminderLog> logs = reminderLogRepository.findByEventId(event.getId());
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getDaysBeforeEvent()).isEqualTo(7);
        assertThat(logs.get(0).getStatus()).isEqualTo(ReminderLog.ReminderStatus.SENT);
    }

    @Test
    @DisplayName("3일 전 리마인더가 정상적으로 발송됨")
    void testSendDailyReminders_3DaysBefore() {
        // Given: 3일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(3);
        Event event = Event.builder()
                .user(testUser)
                .title("테스트 이벤트")
                .description("3일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isTracking(true)
                .build();

        // 3일 전 리마인더 추가
        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        event.addReminder(reminder);
        eventRepository.save(event);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 리마인더가 발송되었는지 확인
        verify(emailService, times(1))
                .sendReminderEmail(eq(testUser.getEmail()), contains("3일 전"), anyString());
    }

    @Test
    @DisplayName("1일 전 리마인더가 정상적으로 발송됨")
    void testSendDailyReminders_1DayBefore() {
        // Given: 1일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(1);
        Event event = Event.builder()
                .user(testUser)
                .title("내일의 이벤트")
                .description("1일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.VALENTINES_DAY)
                .isTracking(true)
                .build();

        // 1일 전 리마인더 추가
        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(1)
                .isActive(true)
                .build();
        event.addReminder(reminder);
        eventRepository.save(event);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 리마인더가 발송되었는지 확인
        verify(emailService, times(1))
                .sendReminderEmail(eq(testUser.getEmail()), contains("1일 전"), anyString());
    }

    @Test
    @DisplayName("비활성화된 리마인더는 발송되지 않음")
    void testSendDailyReminders_InactiveReminderNotSent() {
        // Given: 7일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(7);
        Event event = Event.builder()
                .user(testUser)
                .title("테스트 이벤트")
                .description("7일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .build();

        // 비활성화된 리마인더 추가
        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(false)
                .build();
        event.addReminder(reminder);
        eventRepository.save(event);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 리마인더가 발송되지 않았는지 확인
        verify(emailService, never())
                .sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("추적하지 않는 이벤트는 리마인더가 발송되지 않음")
    void testSendDailyReminders_NotTrackingEventNotSent() {
        // Given: 7일 후에 발생하는 이벤트 생성 (추적 안 함)
        LocalDate eventDate = LocalDate.now().plusDays(7);
        Event event = Event.builder()
                .user(testUser)
                .title("추적 안 하는 이벤트")
                .description("7일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(false)  // 추적 안 함
                .build();

        // 7일 전 리마인더 추가
        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        event.addReminder(reminder);
        eventRepository.save(event);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 리마인더가 발송되지 않았는지 확인
        verify(emailService, never())
                .sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("여러 리마인더가 있는 이벤트에서 올바른 리마인더만 발송됨")
    void testSendDailyReminders_MultipleReminders() {
        // Given: 7일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(7);
        Event event = Event.builder()
                .user(testUser)
                .title("테스트 이벤트")
                .description("7일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .build();

        // 여러 리마인더 추가
        EventReminder reminder7 = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        EventReminder reminder3 = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        EventReminder reminder1 = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(1)
                .isActive(true)
                .build();

        event.addReminder(reminder7);
        event.addReminder(reminder3);
        event.addReminder(reminder1);
        eventRepository.save(event);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 7일 전 리마인더만 발송됨
        verify(emailService, times(1))
                .sendReminderEmail(eq(testUser.getEmail()), contains("7일 전"), anyString());
    }

    @Test
    @DisplayName("여러 이벤트가 있을 때 모든 해당 리마인더가 발송됨")
    void testSendDailyReminders_MultipleEvents() {
        // Given: 여러 이벤트 생성
        LocalDate eventDate1 = LocalDate.now().plusDays(7);
        Event event1 = Event.builder()
                .user(testUser)
                .title("이벤트 1")
                .description("7일 후 이벤트 1")
                .eventDate(eventDate1)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .build();
        EventReminder reminder1 = EventReminder.builder()
                .event(event1)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        event1.addReminder(reminder1);
        eventRepository.save(event1);

        LocalDate eventDate2 = LocalDate.now().plusDays(3);
        Event event2 = Event.builder()
                .user(testUser)
                .title("이벤트 2")
                .description("3일 후 이벤트 2")
                .eventDate(eventDate2)
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isTracking(true)
                .build();
        EventReminder reminder2 = EventReminder.builder()
                .event(event2)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        event2.addReminder(reminder2);
        eventRepository.save(event2);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 두 리마인더가 모두 발송됨
        verify(emailService, times(2))
                .sendReminderEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    @DisplayName("중복 리마인더는 24시간 내에 재발송되지 않음")
    void testSendDailyReminders_NoDuplicateWithin24Hours() {
        // Given: 7일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(7);
        Event event = Event.builder()
                .user(testUser)
                .title("테스트 이벤트")
                .description("7일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .build();

        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        event.addReminder(reminder);
        event = eventRepository.save(event);

        // 이미 리마인더 로그 생성 (1시간 전)
        ReminderLog existingLog = ReminderLog.builder()
                .event(event)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusHours(1))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        reminderLogRepository.save(existingLog);

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 리마인더가 재발송되지 않음
        verify(emailService, never())
                .sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("이메일 발송 실패 시 실패 로그가 생성됨")
    void testSendDailyReminders_EmailFailureLogged() {
        // Given: 7일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(7);
        Event event = Event.builder()
                .user(testUser)
                .title("테스트 이벤트")
                .description("7일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .build();

        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        event.addReminder(reminder);
        event = eventRepository.save(event);

        // 이메일 발송 실패 시뮬레이션
        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendReminderEmail(anyString(), anyString(), anyString());

        // When: 스케줄러 실행
        reminderService.sendDailyReminders();

        // Then: 실패 로그가 생성됨
        List<ReminderLog> logs = reminderLogRepository.findByEventId(event.getId());
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getStatus()).isEqualTo(ReminderLog.ReminderStatus.FAILED);
    }

    @Test
    @DisplayName("즉시 리마인더 발송이 정상적으로 동작함")
    void testSendImmediateReminder() {
        // Given: 7일 후에 발생하는 이벤트 생성
        LocalDate eventDate = LocalDate.now().plusDays(7);
        Event event = Event.builder()
                .user(testUser)
                .title("테스트 이벤트")
                .description("7일 후 이벤트")
                .eventDate(eventDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .build();

        EventReminder reminder = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        event.addReminder(reminder);
        event = eventRepository.save(event);

        // When: 즉시 리마인더 발송
        reminderService.sendImmediateReminder(event.getId());

        // Then: 리마인더가 발송됨
        verify(emailService, times(1))
                .sendReminderEmail(eq(testUser.getEmail()), anyString(), anyString());
    }
}
