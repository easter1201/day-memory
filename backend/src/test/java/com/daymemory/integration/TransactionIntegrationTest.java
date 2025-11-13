package com.daymemory.integration;

import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.dto.UserDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.EventReminderRepository;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.service.EventService;
import com.daymemory.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 통합 테스트
 * - @Transactional 동작 확인
 * - 트랜잭션 롤백 동작 확인
 * - 트랜잭션 전파 동작 확인
 * - 데이터 정합성 확인
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("트랜잭션 통합 테스트")
class TransactionIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventReminderRepository eventReminderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("transaction.test@example.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("Transaction Test User")
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("@Transactional 메서드에서 데이터가 정상적으로 저장됨")
    void testTransactionalSaveSuccess() {
        // Given: 이벤트 생성 요청
        EventDto.Request request = EventDto.Request.builder()
                .title("트랜잭션 테스트 이벤트")
                .description("트랜잭션 테스트")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .reminderDays(List.of(7, 3, 1))
                .build();

        // When: 이벤트 생성
        EventDto.Response response = eventService.createEvent(testUser.getId(), request);

        // Then: 데이터베이스에 저장됨
        Optional<Event> savedEvent = eventRepository.findById(response.getId());
        assertThat(savedEvent).isPresent();
        assertThat(savedEvent.get().getTitle()).isEqualTo("트랜잭션 테스트 이벤트");

        // 리마인더도 저장됨
        Event eventWithReminders = eventRepository.findByIdWithUserAndReminders(response.getId()).orElseThrow();
        assertThat(eventWithReminders.getReminders()).hasSize(3);
    }

    @Test
    @DisplayName("트랜잭션 내에서 예외 발생 시 롤백됨")
    void testTransactionalRollbackOnException() {
        // Given: 존재하지 않는 사용자 ID
        Long invalidUserId = 99999L;

        EventDto.Request request = EventDto.Request.builder()
                .title("롤백 테스트 이벤트")
                .description("예외 발생 시 롤백됨")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();

        // When & Then: 예외 발생
        assertThatThrownBy(() -> eventService.createEvent(invalidUserId, request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

        // 데이터베이스에 저장되지 않음
        List<Event> events = eventRepository.findAll();
        long rollbackEventCount = events.stream()
                .filter(e -> e.getTitle().equals("롤백 테스트 이벤트"))
                .count();
        assertThat(rollbackEventCount).isEqualTo(0);
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 시 롤백됨")
    void testSignupRollbackOnDuplicateEmail() {
        // Given: 첫 번째 사용자 생성
        UserDto.SignupRequest firstRequest = UserDto.SignupRequest.builder()
                .email("duplicate@example.com")
                .password("password123")
                .nickname("First User")
                .build();
        userService.signup(firstRequest);

        // When & Then: 동일한 이메일로 두 번째 가입 시도
        UserDto.SignupRequest secondRequest = UserDto.SignupRequest.builder()
                .email("duplicate@example.com")
                .password("password456")
                .nickname("Second User")
                .build();

        assertThatThrownBy(() -> userService.signup(secondRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_ALREADY_EXISTS.getMessage());

        // 첫 번째 사용자만 존재함
        List<User> users = userRepository.findAll();
        long duplicateCount = users.stream()
                .filter(u -> u.getEmail().equals("duplicate@example.com"))
                .count();
        assertThat(duplicateCount).isEqualTo(1);
    }

    @Test
    @DisplayName("이벤트 생성 시 리마인더가 함께 저장됨 (트랜잭션 일관성)")
    void testEventAndRemindersSavedInSameTransaction() {
        // Given: 이벤트 생성 요청
        EventDto.Request request = EventDto.Request.builder()
                .title("일관성 테스트 이벤트")
                .description("이벤트와 리마인더 일괄 저장")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isRecurring(false)
                .reminderDays(List.of(30, 14, 7, 3, 1))
                .build();

        // When: 이벤트 생성
        EventDto.Response response = eventService.createEvent(testUser.getId(), request);

        // Then: 이벤트와 리마인더가 모두 저장됨
        Event savedEvent = eventRepository.findByIdWithUserAndReminders(response.getId())
                .orElseThrow();

        assertThat(savedEvent.getTitle()).isEqualTo("일관성 테스트 이벤트");
        assertThat(savedEvent.getReminders()).hasSize(5);

        // 리마인더 일수 확인
        List<Integer> reminderDays = savedEvent.getReminders().stream()
                .map(EventReminder::getDaysBeforeEvent)
                .sorted()
                .toList();
        assertThat(reminderDays).containsExactly(1, 3, 7, 14, 30);
    }

    @Test
    @DisplayName("이벤트 업데이트가 트랜잭션 내에서 정상 동작함")
    void testEventUpdateInTransaction() {
        // Given: 이벤트 생성
        Event event = Event.builder()
                .user(testUser)
                .title("업데이트 전 제목")
                .description("업데이트 전 설명")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();
        event = eventRepository.save(event);

        // When: 이벤트 업데이트
        EventDto.Request updateRequest = EventDto.Request.builder()
                .title("업데이트 후 제목")
                .description("업데이트 후 설명")
                .eventDate(LocalDate.now().plusDays(60))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isRecurring(true)
                .build();

        eventService.updateEvent(event.getId(), updateRequest);

        // Then: 변경사항이 저장됨
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updatedEvent.getTitle()).isEqualTo("업데이트 후 제목");
        assertThat(updatedEvent.getDescription()).isEqualTo("업데이트 후 설명");
        assertThat(updatedEvent.getEventType()).isEqualTo(Event.EventType.ANNIVERSARY_100);
        assertThat(updatedEvent.getIsRecurring()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 업데이트 시 예외 발생 및 롤백")
    void testUpdateNonExistentEventThrowsException() {
        // Given: 존재하지 않는 이벤트 ID
        Long invalidEventId = 99999L;

        EventDto.Request updateRequest = EventDto.Request.builder()
                .title("업데이트 시도")
                .description("존재하지 않는 이벤트")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();

        // When & Then: 예외 발생
        assertThatThrownBy(() -> eventService.updateEvent(invalidEventId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EVENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이벤트 삭제 시 연관된 리마인더도 함께 삭제됨 (CASCADE)")
    void testEventDeletionCascadesToReminders() {
        // Given: 리마인더가 있는 이벤트 생성
        Event event = Event.builder()
                .user(testUser)
                .title("삭제 테스트 이벤트")
                .description("CASCADE 테스트")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();

        EventReminder reminder1 = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();

        EventReminder reminder2 = EventReminder.builder()
                .event(event)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();

        event.addReminder(reminder1);
        event.addReminder(reminder2);
        event = eventRepository.save(event);

        Long eventId = event.getId();

        // 리마인더가 존재하는지 확인
        Event eventWithReminders = eventRepository.findByIdWithUserAndReminders(eventId).orElseThrow();
        assertThat(eventWithReminders.getReminders()).hasSize(2);

        // When: 이벤트 삭제
        eventService.deleteEvent(eventId);

        // Then: 이벤트와 리마인더가 모두 삭제됨
        Optional<Event> deletedEvent = eventRepository.findById(eventId);
        assertThat(deletedEvent).isEmpty();

        // 리마인더도 삭제됨 (CASCADE)
        List<EventReminder> allReminders = eventReminderRepository.findAll();
        boolean hasReminderForDeletedEvent = allReminders.stream()
                .anyMatch(r -> r.getEvent().getId().equals(eventId));
        assertThat(hasReminderForDeletedEvent).isFalse();
    }

    @Test
    @DisplayName("트랜잭션 내에서 여러 엔티티를 동시에 저장할 수 있음")
    void testSaveMultipleEntitiesInTransaction() {
        // Given: 여러 이벤트 생성 요청
        EventDto.Request request1 = EventDto.Request.builder()
                .title("이벤트 1")
                .description("첫 번째 이벤트")
                .eventDate(LocalDate.now().plusDays(10))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();

        EventDto.Request request2 = EventDto.Request.builder()
                .title("이벤트 2")
                .description("두 번째 이벤트")
                .eventDate(LocalDate.now().plusDays(20))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isRecurring(false)
                .build();

        EventDto.Request request3 = EventDto.Request.builder()
                .title("이벤트 3")
                .description("세 번째 이벤트")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.VALENTINES_DAY)
                .isRecurring(false)
                .build();

        // When: 여러 이벤트 생성
        eventService.createEvent(testUser.getId(), request1);
        eventService.createEvent(testUser.getId(), request2);
        eventService.createEvent(testUser.getId(), request3);

        // Then: 모든 이벤트가 저장됨
        List<Event> userEvents = eventRepository.findByUserIdAndIsActiveTrue(testUser.getId());
        assertThat(userEvents).hasSize(3);

        assertThat(userEvents)
                .extracting(Event::getTitle)
                .containsExactlyInAnyOrder("이벤트 1", "이벤트 2", "이벤트 3");
    }

    @Test
    @DisplayName("읽기 전용 트랜잭션에서 조회만 가능함")
    void testReadOnlyTransaction() {
        // Given: 이벤트 생성
        Event event = Event.builder()
                .user(testUser)
                .title("읽기 전용 테스트")
                .description("조회 테스트")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();
        event = eventRepository.save(event);

        // When: 읽기 전용 메서드로 조회
        EventDto.Response response = eventService.getEvent(event.getId());

        // Then: 조회 성공
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("읽기 전용 테스트");
    }

    @Test
    @DisplayName("중첩 트랜잭션이 정상적으로 동작함")
    void testNestedTransaction() {
        // Given: 사용자와 이벤트를 모두 생성하는 시나리오
        UserDto.SignupRequest userRequest = UserDto.SignupRequest.builder()
                .email("nested@example.com")
                .password("password123")
                .nickname("Nested Transaction User")
                .build();

        // When: 사용자 생성 (트랜잭션 1)
        UserDto.Response userResponse = userService.signup(userRequest);

        // 생성된 사용자로 이벤트 생성 (트랜잭션 2)
        EventDto.Request eventRequest = EventDto.Request.builder()
                .title("중첩 트랜잭션 이벤트")
                .description("중첩 트랜잭션 테스트")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .build();

        EventDto.Response eventResponse = eventService.createEvent(userResponse.getId(), eventRequest);

        // Then: 모두 정상적으로 저장됨
        Optional<User> savedUser = userRepository.findById(userResponse.getId());
        assertThat(savedUser).isPresent();

        Optional<Event> savedEvent = eventRepository.findById(eventResponse.getId());
        assertThat(savedEvent).isPresent();
        assertThat(savedEvent.get().getUser().getId()).isEqualTo(userResponse.getId());
    }

    @Test
    @DisplayName("동일 트랜잭션 내에서 변경된 데이터를 즉시 조회 가능함")
    void testDataVisibilityWithinSameTransaction() {
        // Given & When: 이벤트 생성
        EventDto.Request request = EventDto.Request.builder()
                .title("가시성 테스트")
                .description("동일 트랜잭션 내 조회")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .reminderDays(List.of(7, 3, 1))
                .build();

        EventDto.Response createdEvent = eventService.createEvent(testUser.getId(), request);

        // Then: 동일 트랜잭션 내에서 즉시 조회 가능
        EventDto.Response retrievedEvent = eventService.getEvent(createdEvent.getId());
        assertThat(retrievedEvent.getId()).isEqualTo(createdEvent.getId());
        assertThat(retrievedEvent.getTitle()).isEqualTo("가시성 테스트");
        assertThat(retrievedEvent.getReminders()).hasSize(3);
    }
}
