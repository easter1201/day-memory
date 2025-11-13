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

    @Query(value = "SELECT er.* FROM event_reminders er " +
           "JOIN events e ON er.event_id = e.id " +
           "WHERE e.user_id = :userId " +
           "AND er.is_active = true " +
           "AND (e.event_date - :today) = er.days_before_event",
           nativeQuery = true)
    List<EventReminder> findTodayRemindersByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDate today);
}
