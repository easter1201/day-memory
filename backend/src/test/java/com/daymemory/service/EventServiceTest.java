package com.daymemory.service;

import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService 테스트")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private Event testEvent;
    private EventDto.Request createRequest;

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
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .isTracking(true)
                .reminders(new ArrayList<>())
                .build();

        // Given: 이벤트 생성 요청 DTO
        createRequest = EventDto.Request.builder()
                .title("생일")
                .description("친구 생일")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .isTracking(true)
                .reminderDays(List.of(30, 7, 1))
                .build();
    }

    @Test
    @DisplayName("이벤트 생성 성공")
    void testCreateEvent_Success() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.save(any(Event.class))).willReturn(testEvent);

        // When
        EventDto.Response response = eventService.createEvent(1L, createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("생일");
        assertThat(response.getEventType()).isEqualTo(Event.EventType.BIRTHDAY);

        // Verify: 메서드 호출 검증
        then(userRepository).should(times(1)).findById(1L);
        then(eventRepository).should(times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("이벤트 생성 - 존재하지 않는 사용자")
    void testCreateEvent_UserNotFound() {
        // Given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(999L, createRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        // Verify
        then(eventRepository).should(never()).save(any(Event.class));
    }

    @Test
    @DisplayName("이벤트 조회 성공")
    void testGetEvent_Success() {
        // Given
        given(eventRepository.findByIdWithUserAndReminders(1L))
                .willReturn(Optional.of(testEvent));

        // When
        EventDto.Response response = eventService.getEvent(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("생일");

        // Verify
        then(eventRepository).should(times(1)).findByIdWithUserAndReminders(1L);
    }

    @Test
    @DisplayName("이벤트 조회 - 존재하지 않는 이벤트")
    void testGetEvent_NotFound() {
        // Given
        given(eventRepository.findByIdWithUserAndReminders(999L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.getEvent(999L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("이벤트 수정 성공")
    void testUpdateEvent_Success() {
        // Given
        EventDto.Request updateRequest = EventDto.Request.builder()
                .title("수정된 생일")
                .description("수정된 설명")
                .eventDate(LocalDate.now().plusDays(40))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(true)
                .reminderDays(List.of(14, 3))
                .build();

        given(eventRepository.findByIdWithUserAndReminders(1L))
                .willReturn(Optional.of(testEvent));

        // When
        EventDto.Response response = eventService.updateEvent(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();

        // Verify: update 메서드가 호출되었는지 확인
        then(eventRepository).should(times(1)).findByIdWithUserAndReminders(1L);
    }

    @Test
    @DisplayName("이벤트 수정 - 존재하지 않는 이벤트")
    void testUpdateEvent_NotFound() {
        // Given
        given(eventRepository.findByIdWithUserAndReminders(999L))
                .willReturn(Optional.empty());

        EventDto.Request updateRequest = EventDto.Request.builder()
                .title("수정된 생일")
                .build();

        // When & Then
        assertThatThrownBy(() -> eventService.updateEvent(999L, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("이벤트 삭제 (비활성화) 성공")
    void testDeleteEvent_Success() {
        // Given
        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));

        // When
        eventService.deleteEvent(1L);

        // Then
        // Verify: deactivate가 호출되었는지 확인
        then(eventRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("이벤트 삭제 - 존재하지 않는 이벤트")
    void testDeleteEvent_NotFound() {
        // Given
        given(eventRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.deleteEvent(999L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("추적 상태 토글 성공")
    void testToggleTracking_Success() {
        // Given
        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));

        // When
        EventDto.Response response = eventService.toggleTracking(1L, false);

        // Then
        assertThat(response).isNotNull();

        // Verify
        then(eventRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("다가오는 이벤트 조회")
    void testGetUpcomingEvents() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        Event event1 = Event.builder()
                .id(1L)
                .user(testUser)
                .title("이벤트 1")
                .eventDate(today.plusDays(5))
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .reminders(new ArrayList<>())
                .build();

        Event event2 = Event.builder()
                .id(2L)
                .user(testUser)
                .title("이벤트 2")
                .eventDate(today.plusDays(15))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isTracking(true)
                .reminders(new ArrayList<>())
                .build();

        List<Event> upcomingEvents = List.of(event1, event2);
        given(eventRepository.findUpcomingEvents(1L, today, endDate))
                .willReturn(upcomingEvents);

        // When
        List<EventDto.Response> responses = eventService.getUpcomingEvents(1L, 30);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("이벤트 1");
        assertThat(responses.get(1).getTitle()).isEqualTo("이벤트 2");

        // Verify
        then(eventRepository).should(times(1)).findUpcomingEvents(eq(1L), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("사용자별 이벤트 조회")
    void testGetEventsByUser() {
        // Given
        List<Event> userEvents = List.of(testEvent);
        given(eventRepository.findByUserIdAndIsActiveTrue(1L)).willReturn(userEvents);

        // When
        List<EventDto.Response> responses = eventService.getEventsByUser(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("생일");

        // Verify
        then(eventRepository).should(times(1)).findByUserIdAndIsActiveTrue(1L);
    }

    @Test
    @DisplayName("이벤트 타입별 조회")
    void testGetEventsByUserAndType() {
        // Given
        List<Event> birthdayEvents = List.of(testEvent);
        given(eventRepository.findByUserIdAndEventTypeAndIsActiveTrue(1L, Event.EventType.BIRTHDAY))
                .willReturn(birthdayEvents);

        // When
        List<EventDto.Response> responses = eventService.getEventsByUserAndType(1L, Event.EventType.BIRTHDAY);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getEventType()).isEqualTo(Event.EventType.BIRTHDAY);

        // Verify
        then(eventRepository).should(times(1))
                .findByUserIdAndEventTypeAndIsActiveTrue(1L, Event.EventType.BIRTHDAY);
    }

    @Test
    @DisplayName("리마인더 업데이트 성공")
    void testUpdateReminders_Success() {
        // Given
        EventDto.UpdateReminderRequest reminderRequest = EventDto.UpdateReminderRequest.builder()
                .reminderDays(List.of(7, 3, 1))
                .build();

        given(eventRepository.findByIdWithUserAndReminders(1L))
                .willReturn(Optional.of(testEvent));

        // When
        EventDto.Response response = eventService.updateReminders(1L, reminderRequest);

        // Then
        assertThat(response).isNotNull();

        // Verify
        then(eventRepository).should(times(1)).findByIdWithUserAndReminders(1L);
    }

    @Test
    @DisplayName("특정 날짜의 리마인더가 필요한 이벤트 조회")
    void testGetEventsRequiringReminder() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(30);
        List<Event> events = List.of(testEvent);
        given(eventRepository.findEventsByDate(targetDate)).willReturn(events);

        // When
        List<Event> result = eventService.getEventsRequiringReminder(targetDate);

        // Then
        assertThat(result).hasSize(1);

        // Verify
        then(eventRepository).should(times(1)).findEventsByDate(targetDate);
    }

    @Test
    @DisplayName("이벤트 생성 - 기본 리마인더 설정")
    void testCreateEvent_WithDefaultReminders() {
        // Given
        EventDto.Request requestWithoutReminders = EventDto.Request.builder()
                .title("생일")
                .description("친구 생일")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.save(any(Event.class))).willReturn(testEvent);

        // When
        EventDto.Response response = eventService.createEvent(1L, requestWithoutReminders);

        // Then
        assertThat(response).isNotNull();

        // Verify: 기본 리마인더(30, 7, 1일)가 설정되어야 함
        then(eventRepository).should(times(1)).save(any(Event.class));
    }
}
