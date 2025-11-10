package com.daymemory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminder_logs", indexes = {
    @Index(name = "idx_event_days_sent", columnList = "event_id, days_before_event, sent_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReminderLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // 이벤트 며칠 전 리마인더인지 (유연한 설정 지원)
    @Column(nullable = false)
    private Integer daysBeforeEvent;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReminderStatus status = ReminderStatus.SENT;

    public enum ReminderStatus {
        SENT,
        FAILED
    }

    public void markAsFailed() {
        this.status = ReminderStatus.FAILED;
    }
}
