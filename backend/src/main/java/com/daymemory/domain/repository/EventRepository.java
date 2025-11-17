package com.daymemory.domain.repository;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // N+1 문제 방지: User만 fetch join
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "WHERE e.user = :user AND e.isActive = true")
    List<Event> findByUserAndIsActiveTrue(@Param("user") User user);

    // N+1 문제 방지: User만 fetch join
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "WHERE e.user.id = :userId AND e.isActive = true")
    List<Event> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);

    // N+1 문제 방지: User만 fetch join + 이벤트 타입 필터링
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "WHERE e.user.id = :userId AND e.eventType = :eventType AND e.isActive = true")
    List<Event> findByUserIdAndEventTypeAndIsActiveTrue(@Param("userId") Long userId,
                                                         @Param("eventType") Event.EventType eventType);

    // 반복 이벤트 조회 (지난 이벤트 포함)
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "WHERE e.user.id = :userId AND e.isRecurring = true AND e.isActive = true")
    List<Event> findByUserIdAndIsRecurringTrue(@Param("userId") Long userId);

    // N+1 문제 방지: User와 Reminders를 fetch join
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "LEFT JOIN FETCH e.reminders " +
           "WHERE e.user.id = :userId AND e.isActive = true " +
           "AND e.eventDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("userId") Long userId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    // N+1 문제 방지: User와 Reminders를 fetch join
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "LEFT JOIN FETCH e.reminders r " +
           "WHERE e.isActive = true AND e.isTracking = true " +
           "AND e.eventDate = :targetDate " +
           "AND r.isActive = true")
    List<Event> findEventsByDate(@Param("targetDate") LocalDate targetDate);

    // 특정 ID의 Event를 User, Reminders와 함께 조회 (N+1 방지)
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "LEFT JOIN FETCH e.reminders " +
           "WHERE e.id = :eventId")
    Optional<Event> findByIdWithUserAndReminders(@Param("eventId") Long eventId);

    // 특정 날짜 범위의 이벤트 조회 (tracking 중인 것만)
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "WHERE e.isActive = true AND e.isTracking = true " +
           "AND e.eventDate BETWEEN :startDate AND :endDate")
    List<Event> findTrackingEventsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 모든 반복 이벤트를 Reminders와 함께 조회 (N+1 방지)
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.user " +
           "LEFT JOIN FETCH e.reminders " +
           "WHERE e.isRecurring = true AND e.isActive = true")
    List<Event> findAllRecurringEventsWithReminders();
}
