package com.daymemory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_reminders", indexes = {
    @Index(name = "idx_reminder_event_id", columnList = "event_id"),
    @Index(name = "idx_reminder_days", columnList = "days_before_event, is_active"),
    @Index(name = "idx_reminder_event_days", columnList = "event_id, days_before_event, is_active")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventReminder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Integer daysBeforeEvent;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateDaysBeforeEvent(Integer daysBeforeEvent) {
        this.daysBeforeEvent = daysBeforeEvent;
    }
}
