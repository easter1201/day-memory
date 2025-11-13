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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("EventReminderRepository 테스트")
class EventReminderRepositoryTest {

    @Autowired
    private EventReminderRepository eventReminderRepository;

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
        eventReminderRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이벤트별 활성 리마인더 조회")
    void testFindByEventIdAndIsActiveTrue() {
        // given
        EventReminder activeReminder1 = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(activeReminder1);

        EventReminder activeReminder2 = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        eventReminderRepository.save(activeReminder2);

        EventReminder inactiveReminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(1)
                .isActive(false)
                .build();
        eventReminderRepository.save(inactiveReminder);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> activeReminders = eventReminderRepository.findByEventIdAndIsActiveTrue(
                testEvent.getId());

        // then
        assertThat(activeReminders).hasSize(2);
        assertThat(activeReminders).allMatch(EventReminder::getIsActive);
        assertThat(activeReminders).extracting("daysBeforeEvent")
                .containsExactlyInAnyOrder(7, 3);
    }

    @Test
    @DisplayName("이벤트와 일수로 활성 리마인더 조회")
    void testFindActiveRemindersByEventAndDays() {
        // given
        EventReminder reminder7Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder7Days);

        EventReminder reminder3Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder3Days);

        EventReminder inactive7Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(false)
                .build();
        eventReminderRepository.save(inactive7Days);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> reminders = eventReminderRepository.findActiveRemindersByEventAndDays(
                testEvent.getId(), 7);

        // then
        assertThat(reminders).hasSize(1);
        assertThat(reminders.get(0).getDaysBeforeEvent()).isEqualTo(7);
        assertThat(reminders.get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("특정 일수의 모든 활성 리마인더 조회")
    void testFindAllActiveRemindersByDays() {
        // given
        Event anotherEvent = Event.builder()
                .user(testUser)
                .title("Anniversary")
                .eventDate(LocalDate.now().plusDays(60))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isActive(true)
                .build();
        anotherEvent = eventRepository.save(anotherEvent);

        EventReminder reminder1 = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder1);

        EventReminder reminder2 = EventReminder.builder()
                .event(anotherEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder2);

        EventReminder reminder3Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder3Days);

        EventReminder inactive7Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(false)
                .build();
        eventReminderRepository.save(inactive7Days);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> reminders7Days = eventReminderRepository.findAllActiveRemindersByDays(7);

        // then
        assertThat(reminders7Days).hasSize(2);
        assertThat(reminders7Days).allMatch(r -> r.getDaysBeforeEvent() == 7);
        assertThat(reminders7Days).allMatch(EventReminder::getIsActive);
    }

    @Test
    @DisplayName("리마인더 활성화/비활성화 테스트")
    void testActivateAndDeactivate() {
        // given
        EventReminder reminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        EventReminder savedReminder = eventReminderRepository.save(reminder);
        assertThat(savedReminder.getIsActive()).isTrue();

        // when - 비활성화
        savedReminder.deactivate();
        EventReminder deactivatedReminder = eventReminderRepository.save(savedReminder);

        // then
        assertThat(deactivatedReminder.getIsActive()).isFalse();

        // when - 재활성화
        deactivatedReminder.activate();
        EventReminder reactivatedReminder = eventReminderRepository.save(deactivatedReminder);

        // then
        assertThat(reactivatedReminder.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("리마인더 일수 업데이트 테스트")
    void testUpdateDaysBeforeEvent() {
        // given
        EventReminder reminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        EventReminder savedReminder = eventReminderRepository.save(reminder);
        assertThat(savedReminder.getDaysBeforeEvent()).isEqualTo(7);

        // when
        savedReminder.updateDaysBeforeEvent(14);
        EventReminder updatedReminder = eventReminderRepository.save(savedReminder);

        // then
        assertThat(updatedReminder.getDaysBeforeEvent()).isEqualTo(14);
    }

    @Test
    @DisplayName("리마인더 생성 시 기본값은 활성 상태")
    void testDefaultIsActiveTrue() {
        // given
        EventReminder reminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .build();

        // when
        EventReminder savedReminder = eventReminderRepository.save(reminder);

        // then
        assertThat(savedReminder.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("이벤트에 여러 개의 리마인더 설정 가능")
    void testMultipleRemindersPerEvent() {
        // given
        EventReminder reminder30Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(30)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder30Days);

        EventReminder reminder14Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(14)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder14Days);

        EventReminder reminder7Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder7Days);

        EventReminder reminder3Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder3Days);

        EventReminder reminder1Day = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(1)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder1Day);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> reminders = eventReminderRepository.findByEventIdAndIsActiveTrue(
                testEvent.getId());

        // then
        assertThat(reminders).hasSize(5);
        assertThat(reminders).extracting("daysBeforeEvent")
                .containsExactlyInAnyOrder(30, 14, 7, 3, 1);
    }

    @Test
    @DisplayName("서로 다른 이벤트의 리마인더는 독립적으로 관리됨")
    void testRemindersAreIndependentPerEvent() {
        // given
        Event anotherEvent = Event.builder()
                .user(testUser)
                .title("Anniversary")
                .eventDate(LocalDate.now().plusDays(60))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isActive(true)
                .build();
        anotherEvent = eventRepository.save(anotherEvent);

        EventReminder reminder1 = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder1);

        EventReminder reminder2 = EventReminder.builder()
                .event(anotherEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> testEventReminders = eventReminderRepository.findByEventIdAndIsActiveTrue(
                testEvent.getId());
        List<EventReminder> anotherEventReminders = eventReminderRepository.findByEventIdAndIsActiveTrue(
                anotherEvent.getId());

        // then
        assertThat(testEventReminders).hasSize(1);
        assertThat(anotherEventReminders).hasSize(1);
        assertThat(testEventReminders.get(0).getEvent().getId()).isEqualTo(testEvent.getId());
        assertThat(anotherEventReminders.get(0).getEvent().getId()).isEqualTo(anotherEvent.getId());
    }

    @Test
    @DisplayName("비활성화된 리마인더는 활성 리마인더 조회에 포함되지 않음")
    void testInactiveRemindersNotIncluded() {
        // given
        EventReminder activeReminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(activeReminder);

        EventReminder inactiveReminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(3)
                .isActive(false)
                .build();
        eventReminderRepository.save(inactiveReminder);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> activeReminders = eventReminderRepository.findByEventIdAndIsActiveTrue(
                testEvent.getId());

        // then
        assertThat(activeReminders).hasSize(1);
        assertThat(activeReminders.get(0).getDaysBeforeEvent()).isEqualTo(7);
    }

    @Test
    @DisplayName("같은 일수의 리마인더 중복 설정 가능 (활성/비활성 구분)")
    void testDuplicateDaysWithDifferentActiveStatus() {
        // given
        EventReminder activeReminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(activeReminder);

        EventReminder inactiveReminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(false)
                .build();
        eventReminderRepository.save(inactiveReminder);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> allReminders = eventReminderRepository.findActiveRemindersByEventAndDays(
                testEvent.getId(), 7);

        // then
        assertThat(allReminders).hasSize(1);
        assertThat(allReminders.get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("커스텀 리마인더 일수 지원 (1일, 30일, 60일 등)")
    void testCustomReminderDays() {
        // given
        EventReminder reminder1Day = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(1)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder1Day);

        EventReminder reminder30Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(30)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder30Days);

        EventReminder reminder60Days = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(60)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder60Days);

        entityManager.flush();
        entityManager.clear();

        // when
        List<EventReminder> reminders = eventReminderRepository.findByEventIdAndIsActiveTrue(
                testEvent.getId());

        // then
        assertThat(reminders).hasSize(3);
        assertThat(reminders).extracting("daysBeforeEvent")
                .containsExactlyInAnyOrder(1, 30, 60);
    }

    @Test
    @DisplayName("이벤트 삭제 시 연관된 리마인더도 함께 삭제됨 (Cascade)")
    void testCascadeDeleteWithEvent() {
        // given
        EventReminder reminder = EventReminder.builder()
                .event(testEvent)
                .daysBeforeEvent(7)
                .isActive(true)
                .build();
        eventReminderRepository.save(reminder);

        Long eventId = testEvent.getId();
        entityManager.flush();
        entityManager.clear();

        // when
        eventRepository.deleteById(eventId);
        entityManager.flush();
        entityManager.clear();

        // then
        List<EventReminder> reminders = eventReminderRepository.findByEventIdAndIsActiveTrue(eventId);
        assertThat(reminders).isEmpty();
    }
}
