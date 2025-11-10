package com.daymemory.domain.repository;

import com.daymemory.domain.entity.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {

    // N+1 문제 방지: Event를 fetch join
    @Query("SELECT DISTINCT rl FROM ReminderLog rl " +
           "LEFT JOIN FETCH rl.event " +
           "WHERE rl.event.id = :eventId")
    List<ReminderLog> findByEventId(@Param("eventId") Long eventId);

    // 특정 이벤트의 특정 일수의 최근 리마인더 조회
    @Query("SELECT rl FROM ReminderLog rl " +
           "WHERE rl.event.id = :eventId " +
           "AND rl.daysBeforeEvent = :daysBeforeEvent " +
           "AND rl.sentAt >= :startDate " +
           "ORDER BY rl.sentAt DESC")
    Optional<ReminderLog> findRecentReminder(
            @Param("eventId") Long eventId,
            @Param("daysBeforeEvent") Integer daysBeforeEvent,
            @Param("startDate") LocalDateTime startDate);

    // 특정 기간 동안 발송된 모든 리마인더 조회
    @Query("SELECT DISTINCT rl FROM ReminderLog rl " +
           "LEFT JOIN FETCH rl.event " +
           "WHERE rl.sentAt BETWEEN :startDate AND :endDate " +
           "ORDER BY rl.sentAt DESC")
    List<ReminderLog> findRemindersBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 실패한 리마인더만 조회
    @Query("SELECT DISTINCT rl FROM ReminderLog rl " +
           "LEFT JOIN FETCH rl.event " +
           "WHERE rl.status = 'FAILED' " +
           "AND rl.sentAt >= :afterDate")
    List<ReminderLog> findFailedRemindersAfter(@Param("afterDate") LocalDateTime afterDate);
}
