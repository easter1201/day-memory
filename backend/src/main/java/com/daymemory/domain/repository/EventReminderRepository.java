package com.daymemory.domain.repository;

import com.daymemory.domain.entity.EventReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {

    List<EventReminder> findByEventIdAndIsActiveTrue(Long eventId);

    @Query("SELECT er FROM EventReminder er " +
           "WHERE er.event.id = :eventId AND er.daysBeforeEvent = :days AND er.isActive = true")
    List<EventReminder> findActiveRemindersByEventAndDays(
            @Param("eventId") Long eventId,
            @Param("days") Integer days);

    @Query("SELECT er FROM EventReminder er " +
           "WHERE er.daysBeforeEvent = :days AND er.isActive = true")
    List<EventReminder> findAllActiveRemindersByDays(@Param("days") Integer days);

    // N+1 문제 방지: Event를 fetch join으로 함께 조회
    @Query("SELECT er FROM EventReminder er " +
           "JOIN FETCH er.event e " +
           "WHERE e.user.id = :userId " +
           "AND er.isActive = true " +
           "AND e.isActive = true " +
           "AND CAST((e.eventDate - :today) AS int) = er.daysBeforeEvent")
    List<EventReminder> findTodayRemindersByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDate today);
}
