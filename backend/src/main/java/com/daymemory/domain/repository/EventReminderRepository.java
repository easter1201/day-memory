package com.daymemory.domain.repository;

import com.daymemory.domain.entity.EventReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
