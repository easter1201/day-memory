package com.daymemory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 100)
    private String recipientName;

    @Column(length = 50)
    private String relationship;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventType eventType = EventType.CUSTOM;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // 사용자가 이 기념일을 챙기는지 여부
    @Column(nullable = false)
    @Builder.Default
    private Boolean isTracking = true;

    // 유연한 리마인더 설정 (1:N 관계)
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventReminder> reminders = new ArrayList<>();

    public enum EventType {
        BIRTHDAY,           // 생일
        ANNIVERSARY_100,    // 100일 기념일
        ANNIVERSARY_200,    // 200일 기념일
        ANNIVERSARY_300,    // 300일 기념일
        ANNIVERSARY_1YEAR,  // 1주년
        ANNIVERSARY_CUSTOM, // 커스텀 기념일 (1000일 등)

        // 커플 기념일 (월별 데이)
        DIARY_DAY,          // 다이어리데이 (1월 14일)
        VALENTINES_DAY,     // 발렌타인데이 (2월 14일)
        WHITE_DAY,          // 화이트데이 (3월 14일)
        BLACK_DAY,          // 블랙데이 (4월 14일)
        ROSE_DAY,           // 로즈데이 (5월 14일)
        KISS_DAY,           // 키스데이 (6월 14일)
        SILVER_DAY,         // 실버데이 (7월 14일)
        GREEN_DAY,          // 그린데이 (8월 14일)
        MUSIC_DAY,          // 뮤직데이/포토데이 (9월 14일)
        WINE_DAY,           // 와인데이 (10월 14일)
        MOVIE_DAY,          // 무비데이/오렌지데이 (11월 14일)
        HUG_DAY,            // 허그데이 (12월 14일)

        // 기타 커플 기념일
        PEPERO_DAY,         // 빼빼로데이 (11월 11일)
        CHRISTMAS_EVE,      // 크리스마스 이브 (12월 24일)
        CHRISTMAS,          // 크리스마스 (12월 25일)
        NEW_YEAR_EVE,       // 새해 전날 (12월 31일)
        NEW_YEAR,           // 새해 (1월 1일)

        // 공휴일 및 기타
        HOLIDAY,            // 기타 공휴일
        VACATION,           // 휴가
        CUSTOM              // 사용자 정의
    }

    public void update(String title, String description, String recipientName, String relationship,
                      LocalDate eventDate, EventType eventType, Boolean isRecurring) {
        this.title = title;
        this.description = description;
        this.recipientName = recipientName;
        this.relationship = relationship;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.isRecurring = isRecurring;
    }

    public void toggleTracking() {
        this.isTracking = !this.isTracking;
    }

    public void setTracking(Boolean isTracking) {
        this.isTracking = isTracking;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    // 리마인더 관리 메서드
    public void addReminder(EventReminder reminder) {
        this.reminders.add(reminder);
    }

    public void removeReminder(EventReminder reminder) {
        this.reminders.remove(reminder);
    }

    public void clearReminders() {
        this.reminders.clear();
    }
}
