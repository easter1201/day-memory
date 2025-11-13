package com.daymemory.service;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.daymemory.domain.entity.ReminderLog;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.ReminderLogRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReminderService 테스트")
class ReminderServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ReminderLogRepository reminderLogRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReminderService reminderService;

    private User testUser;
    private Event testEvent;
    private EventReminder testReminder;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 사용자 설정
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("테스트 사용자")
                .build();

        // Given: 테스트용 이벤트 설정
        testEvent = Event.builder()
                .id(1L)
                .user(testUser)
                .title("생일")
                .description("친구 생일")
                .eventDate(LocalDate.now().plusDays(7))
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .reminders(new ArrayList<>())
                .build();

        // Given: 테스트용 리마인더 설정
        testReminder = EventReminder.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();

        testEvent.addReminder(testReminder);
    }

    @Test
    @DisplayName("일일 리마인더 발송 성공")
    void testSendDailyReminders_Success() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today.plusDays(365);

        List<Event> trackingEvents = List.of(testEvent);
        given(eventRepository.findTrackingEventsBetweenDates(startDate, endDate))
                .willReturn(trackingEvents);
        given(reminderLogRepository.findRecentReminder(anyLong(), anyInt(), any(LocalDateTime.class)))
                .willReturn(Optional.empty());

        String emailContent = "<html>리마인더 내용</html>";
        given(emailService.buildReminderEmailContent(anyString(), anyInt(), anyString())).willReturn(emailContent);
        willDoNothing().given(emailService).sendReminderEmail(anyString(), anyString(), anyString());
        given(reminderLogRepository.save(any(ReminderLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        reminderService.sendDailyReminders();

        // Then
        // Verify: 이메일 발송 확인
        then(emailService).should(atLeastOnce()).sendReminderEmail(
                eq(testUser.getEmail()),
                anyString(),
                anyString()
        );
        then(reminderLogRepository).should(atLeastOnce()).save(any(ReminderLog.class));
    }

    @Test
    @DisplayName("일일 리마인더 발송 - 중복 발송 방지 (24시간)")
    void testSendDailyReminders_NoDuplicates() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today.plusDays(365);

        List<Event> trackingEvents = List.of(testEvent);
        given(eventRepository.findTrackingEventsBetweenDates(startDate, endDate))
                .willReturn(trackingEvents);

        // 최근에 발송된 리마인더 로그가 있음
        ReminderLog recentLog = ReminderLog.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusHours(12))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();
        given(reminderLogRepository.findRecentReminder(anyLong(), anyInt(), any(LocalDateTime.class)))
                .willReturn(Optional.of(recentLog));

        // When
        reminderService.sendDailyReminders();

        // Then
        // Verify: 이메일이 발송되지 않아야 함 (24시간 이내 중복)
        then(emailService).should(never()).sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("일일 리마인더 발송 - 활성 리마인더만 발송")
    void testSendDailyReminders_OnlyActiveReminders() {
        // Given
        // 비활성 리마인더 추가
        EventReminder inactiveReminder = EventReminder.builder()
                .id(2L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(false)
                .build();
        testEvent.addReminder(inactiveReminder);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today.plusDays(365);

        List<Event> trackingEvents = List.of(testEvent);
        given(eventRepository.findTrackingEventsBetweenDates(startDate, endDate))
                .willReturn(trackingEvents);
        given(reminderLogRepository.findRecentReminder(anyLong(), anyInt(), any(LocalDateTime.class)))
                .willReturn(Optional.empty());

        String emailContent = "<html>리마인더 내용</html>";
        given(emailService.buildReminderEmailContent(anyString(), anyInt(), anyString())).willReturn(emailContent);
        willDoNothing().given(emailService).sendReminderEmail(anyString(), anyString(), anyString());
        given(reminderLogRepository.save(any(ReminderLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        reminderService.sendDailyReminders();

        // Then
        // Verify: 활성 리마인더만 발송 (비활성 리마인더는 제외)
        then(emailService).should(atLeastOnce()).sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("일일 리마인더 발송 - 발송 실패 처리")
    void testSendDailyReminders_FailedReminder() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today.plusDays(365);

        List<Event> trackingEvents = List.of(testEvent);
        given(eventRepository.findTrackingEventsBetweenDates(startDate, endDate))
                .willReturn(trackingEvents);
        given(reminderLogRepository.findRecentReminder(anyLong(), anyInt(), any(LocalDateTime.class)))
                .willReturn(Optional.empty());

        String emailContent = "<html>리마인더 내용</html>";
        given(emailService.buildReminderEmailContent(anyString(), anyInt(), anyString())).willReturn(emailContent);

        // 이메일 발송 실패 시뮬레이션
        willThrow(new CustomException(ErrorCode.EMAIL_SEND_FAILED))
                .given(emailService).sendReminderEmail(anyString(), anyString(), anyString());

        given(reminderLogRepository.save(any(ReminderLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        reminderService.sendDailyReminders();

        // Then
        // Verify: 실패 로그가 저장되어야 함
        then(reminderLogRepository).should(atLeastOnce()).save(
                argThat(log -> log.getStatus() == ReminderLog.ReminderStatus.FAILED)
        );
    }

    @Test
    @DisplayName("즉시 리마인더 발송 성공")
    void testSendImmediateReminder() {
        // Given
        given(eventRepository.findByIdWithUserAndReminders(1L))
                .willReturn(Optional.of(testEvent));
        given(reminderLogRepository.findRecentReminder(anyLong(), anyInt(), any(LocalDateTime.class)))
                .willReturn(Optional.empty());

        String emailContent = "<html>리마인더 내용</html>";
        given(emailService.buildReminderEmailContent(anyString(), anyInt(), anyString())).willReturn(emailContent);
        willDoNothing().given(emailService).sendReminderEmail(anyString(), anyString(), anyString());
        given(reminderLogRepository.save(any(ReminderLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        reminderService.sendImmediateReminder(1L);

        // Then
        // Verify
        then(eventRepository).should(times(1)).findByIdWithUserAndReminders(1L);
        then(emailService).should(times(1)).sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("즉시 리마인더 발송 - 존재하지 않는 이벤트")
    void testSendImmediateReminder_EventNotFound() {
        // Given
        given(eventRepository.findByIdWithUserAndReminders(999L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reminderService.sendImmediateReminder(999L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EVENT_NOT_FOUND);

        // Verify
        then(emailService).should(never()).sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("즉시 리마인더 발송 - 이미 지난 이벤트")
    void testSendImmediateReminder_PastEvent() {
        // Given
        Event pastEvent = Event.builder()
                .id(2L)
                .user(testUser)
                .title("지난 이벤트")
                .eventDate(LocalDate.now().minusDays(10))
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .reminders(new ArrayList<>())
                .build();

        given(eventRepository.findByIdWithUserAndReminders(2L))
                .willReturn(Optional.of(pastEvent));

        // When
        reminderService.sendImmediateReminder(2L);

        // Then
        // Verify: 이메일이 발송되지 않아야 함
        then(emailService).should(never()).sendReminderEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("실패한 리마인더 재발송 성공")
    void testRetryFailedReminder() {
        // Given
        ReminderLog failedLog = ReminderLog.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusHours(2))
                .status(ReminderLog.ReminderStatus.FAILED)
                .build();

        given(reminderLogRepository.findById(1L)).willReturn(Optional.of(failedLog));

        String emailContent = "<html>리마인더 내용</html>";
        given(emailService.buildReminderEmailContent(anyString(), anyInt(), anyString())).willReturn(emailContent);
        willDoNothing().given(emailService).sendReminderEmail(anyString(), anyString(), anyString());
        given(reminderLogRepository.save(any(ReminderLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = reminderService.retryFailedReminder(1L);

        // Then
        assertThat(result).isTrue();

        // Verify
        then(emailService).should(times(1)).sendReminderEmail(
                eq(testUser.getEmail()),
                anyString(),
                anyString()
        );
        then(reminderLogRepository).should(times(1)).save(
                argThat(log -> log.getStatus() == ReminderLog.ReminderStatus.SENT)
        );
    }

    @Test
    @DisplayName("실패한 리마인더 재발송 - 존재하지 않는 로그")
    void testRetryFailedReminder_NotFound() {
        // Given
        given(reminderLogRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reminderService.retryFailedReminder(999L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REMINDER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패한 리마인더 재발송 - 이미 발송된 리마인더")
    void testRetryFailedReminder_AlreadySent() {
        // Given
        ReminderLog sentLog = ReminderLog.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusHours(2))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();

        given(reminderLogRepository.findById(1L)).willReturn(Optional.of(sentLog));

        // When & Then
        assertThatThrownBy(() -> reminderService.retryFailedReminder(1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REMINDER_ALREADY_SENT);
    }

    @Test
    @DisplayName("실패한 리마인더 재발송 - 재발송 실패")
    void testRetryFailedReminder_RetryFailed() {
        // Given
        ReminderLog failedLog = ReminderLog.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusHours(2))
                .status(ReminderLog.ReminderStatus.FAILED)
                .build();

        given(reminderLogRepository.findById(1L)).willReturn(Optional.of(failedLog));

        String emailContent = "<html>리마인더 내용</html>";
        given(emailService.buildReminderEmailContent(anyString(), anyInt(), anyString())).willReturn(emailContent);

        // 재발송 실패 시뮬레이션
        willThrow(new CustomException(ErrorCode.EMAIL_SEND_FAILED))
                .given(emailService).sendReminderEmail(anyString(), anyString(), anyString());

        // When
        boolean result = reminderService.retryFailedReminder(1L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("리마인더 로그 조회 - 특정 이벤트")
    void testGetReminderLogs_ByEvent() {
        // Given
        ReminderLog log1 = ReminderLog.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now())
                .status(ReminderLog.ReminderStatus.SENT)
                .build();

        List<ReminderLog> logs = List.of(log1);
        given(reminderLogRepository.findByEventId(1L)).willReturn(logs);

        // When
        List<ReminderLog> result = reminderService.getReminderLogs(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEvent().getId()).isEqualTo(1L);

        // Verify
        then(reminderLogRepository).should(times(1)).findByEventId(1L);
    }

    @Test
    @DisplayName("리마인더 로그 조회 - 최근 30일")
    void testGetReminderLogs_Recent30Days() {
        // Given
        ReminderLog log1 = ReminderLog.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusDays(10))
                .status(ReminderLog.ReminderStatus.SENT)
                .build();

        List<ReminderLog> logs = List.of(log1);
        given(reminderLogRepository.findRemindersBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(logs);

        // When
        List<ReminderLog> result = reminderService.getReminderLogs(null);

        // Then
        assertThat(result).hasSize(1);

        // Verify
        then(reminderLogRepository).should(times(1))
                .findRemindersBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("실패한 리마인더 목록 조회")
    void testGetFailedReminders() {
        // Given
        ReminderLog failedLog = ReminderLog.builder()
                .id(1L)
                .event(testEvent)
                .daysBeforeEvent(7)
                .sentAt(LocalDateTime.now().minusDays(1))
                .status(ReminderLog.ReminderStatus.FAILED)
                .build();

        List<ReminderLog> failedLogs = List.of(failedLog);
        given(reminderLogRepository.findFailedRemindersAfter(any(LocalDateTime.class)))
                .willReturn(failedLogs);

        // When
        List<ReminderLog> result = reminderService.getFailedReminders();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReminderLog.ReminderStatus.FAILED);

        // Verify
        then(reminderLogRepository).should(times(1))
                .findFailedRemindersAfter(any(LocalDateTime.class));
    }
}
