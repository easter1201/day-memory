package com.daymemory.domain.repository;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.EventReminder;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("EventRepository 테스트")
class EventRepositoryTest {

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
                .title("Test Event")
                .description("Test Description")
                .eventDate(LocalDate.now().plusDays(7))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(true)
                .isActive(true)
                .isTracking(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("활성 이벤트만 조회 - User 객체로 조회")
    void testFindByUserAndIsActiveTrue() {
        // given
        Event activeEvent = eventRepository.save(testEvent);

        Event inactiveEvent = Event.builder()
                .user(testUser)
                .title("Inactive Event")
                .eventDate(LocalDate.now().plusDays(10))
                .eventType(Event.EventType.CUSTOM)
                .isActive(false)
                .build();
        eventRepository.save(inactiveEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Event> activeEvents = eventRepository.findByUserAndIsActiveTrue(testUser);

        // then
        assertThat(activeEvents).hasSize(1);
        assertThat(activeEvents.get(0).getTitle()).isEqualTo("Test Event");
        assertThat(activeEvents.get(0).getIsActive()).isTrue();

        // N+1 문제 방지 확인 - User가 이미 fetch join 되어 있어야 함
        assertThat(activeEvents.get(0).getUser()).isNotNull();
        assertThat(activeEvents.get(0).getUser().getNickname()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("활성 이벤트만 조회 - User ID로 조회")
    void testFindByUserIdAndIsActiveTrue() {
        // given
        eventRepository.save(testEvent);

        Event inactiveEvent = Event.builder()
                .user(testUser)
                .title("Inactive Event")
                .eventDate(LocalDate.now().plusDays(10))
                .eventType(Event.EventType.CUSTOM)
                .isActive(false)
                .build();
        eventRepository.save(inactiveEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Event> activeEvents = eventRepository.findByUserIdAndIsActiveTrue(testUser.getId());

        // then
        assertThat(activeEvents).hasSize(1);
        assertThat(activeEvents.get(0).getTitle()).isEqualTo("Test Event");
        assertThat(activeEvents.get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("이벤트 타입별 활성 이벤트 조회")
    void testFindByUserIdAndEventTypeAndIsActiveTrue() {
        // given
        eventRepository.save(testEvent);

        Event customEvent = Event.builder()
                .user(testUser)
                .title("Custom Event")
                .eventDate(LocalDate.now().plusDays(15))
                .eventType(Event.EventType.CUSTOM)
                .isActive(true)
                .build();
        eventRepository.save(customEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Event> birthdayEvents = eventRepository.findByUserIdAndEventTypeAndIsActiveTrue(
                testUser.getId(), Event.EventType.BIRTHDAY);

        // then
        assertThat(birthdayEvents).hasSize(1);
        assertThat(birthdayEvents.get(0).getTitle()).isEqualTo("Test Event");
        assertThat(birthdayEvents.get(0).getEventType()).isEqualTo(Event.EventType.BIRTHDAY);
    }

    @Test
    @DisplayName("반복 이벤트만 조회")
    void testFindByUserIdAndIsRecurringTrue() {
        // given
        eventRepository.save(testEvent);

        Event oneTimeEvent = Event.builder()
                .user(testUser)
                .title("One Time Event")
                .eventDate(LocalDate.now().plusDays(5))
                .eventType(Event.EventType.CUSTOM)
                .isRecurring(false)
                .isActive(true)
                .build();
        eventRepository.save(oneTimeEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Event> recurringEvents = eventRepository.findByUserIdAndIsRecurringTrue(testUser.getId());

        // then
        assertThat(recurringEvents).hasSize(1);
        assertThat(recurringEvents.get(0).getTitle()).isEqualTo("Test Event");
        assertThat(recurringEvents.get(0).getIsRecurring()).isTrue();
    }

    @Test
    @DisplayName("다가오는 이벤트 조회 - 날짜 범위 필터링")
    void testFindUpcomingEvents() {
        // given
        Event event1 = Event.builder()
                .user(testUser)
                .title("Event 1")
                .eventDate(LocalDate.now().plusDays(5))
                .eventType(Event.EventType.BIRTHDAY)
                .isActive(true)
                .build();
        eventRepository.save(event1);

        Event event2 = Event.builder()
                .user(testUser)
                .title("Event 2")
                .eventDate(LocalDate.now().plusDays(10))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isActive(true)
                .build();
        eventRepository.save(event2);

        Event pastEvent = Event.builder()
                .user(testUser)
                .title("Past Event")
                .eventDate(LocalDate.now().minusDays(5))
                .eventType(Event.EventType.CUSTOM)
                .isActive(true)
                .build();
        eventRepository.save(pastEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(15);
        List<Event> upcomingEvents = eventRepository.findUpcomingEvents(
                testUser.getId(), startDate, endDate);

        // then
        assertThat(upcomingEvents).hasSize(2);
        assertThat(upcomingEvents.get(0).getTitle()).isEqualTo("Event 1");
        assertThat(upcomingEvents.get(1).getTitle()).isEqualTo("Event 2");

        // 날짜순 정렬 확인
        assertThat(upcomingEvents.get(0).getEventDate())
                .isBefore(upcomingEvents.get(1).getEventDate());
    }

    @Test
    @DisplayName("특정 ID의 Event를 User와 Reminders와 함께 조회 - Fetch Join 동작 확인")
    void testFindByIdWithUserAndReminders_FetchJoin() {
        // given
        Event savedEvent = eventRepository.save(testEvent);

        // 리마인더 추가
        EventReminder reminder1 = EventReminder.builder()
                .event(savedEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        savedEvent.addReminder(reminder1);

        EventReminder reminder2 = EventReminder.builder()
                .event(savedEvent)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        savedEvent.addReminder(reminder2);

        eventRepository.save(savedEvent);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Event> foundEvent = eventRepository.findByIdWithUserAndReminders(savedEvent.getId());

        // then
        assertThat(foundEvent).isPresent();

        // N+1 문제 방지 확인 - User와 Reminders가 이미 fetch join 되어 있어야 함
        Event event = foundEvent.get();
        assertThat(event.getUser()).isNotNull();
        assertThat(event.getUser().getNickname()).isEqualTo("Test User");

        assertThat(event.getReminders()).hasSize(2);
        assertThat(event.getReminders().get(0).getDaysBeforeEvent()).isIn(7, 3);
        assertThat(event.getReminders().get(1).getDaysBeforeEvent()).isIn(7, 3);
    }

    @Test
    @DisplayName("특정 날짜의 추적 이벤트 조회")
    void testFindEventsByDate() {
        // given
        LocalDate targetDate = LocalDate.now().plusDays(7);

        Event trackingEvent = Event.builder()
                .user(testUser)
                .title("Tracking Event")
                .eventDate(targetDate)
                .eventType(Event.EventType.BIRTHDAY)
                .isActive(true)
                .isTracking(true)
                .build();
        Event savedEvent = eventRepository.save(trackingEvent);

        // 리마인더 추가
        EventReminder reminder = EventReminder.builder()
                .event(savedEvent)
                .daysBeforeEvent(1)
                .isActive(true)
                .build();
        savedEvent.addReminder(reminder);
        eventRepository.save(savedEvent);

        Event notTrackingEvent = Event.builder()
                .user(testUser)
                .title("Not Tracking Event")
                .eventDate(targetDate)
                .eventType(Event.EventType.CUSTOM)
                .isActive(true)
                .isTracking(false)
                .build();
        eventRepository.save(notTrackingEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Event> events = eventRepository.findEventsByDate(targetDate);

        // then
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Tracking Event");
        assertThat(events.get(0).getIsTracking()).isTrue();

        // Reminders도 fetch join 확인
        assertThat(events.get(0).getReminders()).isNotEmpty();
    }

    @Test
    @DisplayName("기간별 추적 이벤트 조회")
    void testFindTrackingEventsBetweenDates() {
        // given
        Event trackingEvent1 = Event.builder()
                .user(testUser)
                .title("Tracking Event 1")
                .eventDate(LocalDate.now().plusDays(5))
                .eventType(Event.EventType.BIRTHDAY)
                .isActive(true)
                .isTracking(true)
                .build();
        eventRepository.save(trackingEvent1);

        Event trackingEvent2 = Event.builder()
                .user(testUser)
                .title("Tracking Event 2")
                .eventDate(LocalDate.now().plusDays(10))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isActive(true)
                .isTracking(true)
                .build();
        eventRepository.save(trackingEvent2);

        Event notTrackingEvent = Event.builder()
                .user(testUser)
                .title("Not Tracking Event")
                .eventDate(LocalDate.now().plusDays(7))
                .eventType(Event.EventType.CUSTOM)
                .isActive(true)
                .isTracking(false)
                .build();
        eventRepository.save(notTrackingEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(15);
        List<Event> trackingEvents = eventRepository.findTrackingEventsBetweenDates(startDate, endDate);

        // then
        assertThat(trackingEvents).hasSize(2);
        assertThat(trackingEvents).extracting("title")
                .containsExactlyInAnyOrder("Tracking Event 1", "Tracking Event 2");
        assertThat(trackingEvents).allMatch(Event::getIsTracking);
    }

    @Test
    @DisplayName("비활성 이벤트는 조회되지 않음")
    void testInactiveEventsNotIncluded() {
        // given
        testEvent.deactivate();
        eventRepository.save(testEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Event> activeEvents = eventRepository.findByUserIdAndIsActiveTrue(testUser.getId());

        // then
        assertThat(activeEvents).isEmpty();
    }

    @Test
    @DisplayName("이벤트 활성화/비활성화 토글 테스트")
    void testEventActivationToggle() {
        // given
        Event savedEvent = eventRepository.save(testEvent);
        assertThat(savedEvent.getIsActive()).isTrue();

        // when - 비활성화
        savedEvent.deactivate();
        Event deactivatedEvent = eventRepository.save(savedEvent);

        // then
        assertThat(deactivatedEvent.getIsActive()).isFalse();

        // when - 재활성화
        deactivatedEvent.activate();
        Event reactivatedEvent = eventRepository.save(deactivatedEvent);

        // then
        assertThat(reactivatedEvent.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("추적 상태 토글 테스트")
    void testTrackingToggle() {
        // given
        Event savedEvent = eventRepository.save(testEvent);
        assertThat(savedEvent.getIsTracking()).isTrue();

        // when
        savedEvent.toggleTracking();
        Event updatedEvent = eventRepository.save(savedEvent);

        // then
        assertThat(updatedEvent.getIsTracking()).isFalse();

        // when
        updatedEvent.toggleTracking();
        Event retoggledEvent = eventRepository.save(updatedEvent);

        // then
        assertThat(retoggledEvent.getIsTracking()).isTrue();
    }

    @Test
    @DisplayName("이벤트 정보 업데이트 테스트")
    void testUpdateEvent() {
        // given
        Event savedEvent = eventRepository.save(testEvent);

        // when
        savedEvent.update(
                "Updated Title",
                "Updated Description",
                "Updated Name",
                "Updated Relationship",
                LocalDate.now().plusDays(30),
                Event.EventType.ANNIVERSARY_1YEAR,
                false
        );
        Event updatedEvent = eventRepository.save(savedEvent);

        // then
        assertThat(updatedEvent.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedEvent.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedEvent.getRecipientName()).isEqualTo("Updated Name");
        assertThat(updatedEvent.getRelationship()).isEqualTo("Updated Relationship");
        assertThat(updatedEvent.getEventDate()).isEqualTo(LocalDate.now().plusDays(30));
        assertThat(updatedEvent.getEventType()).isEqualTo(Event.EventType.ANNIVERSARY_1YEAR);
        assertThat(updatedEvent.getIsRecurring()).isFalse();
    }
}
