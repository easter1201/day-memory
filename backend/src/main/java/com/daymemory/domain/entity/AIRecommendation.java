package com.daymemory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_recommendations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AIRecommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_gender")
    private String recipientGender;

    @Column(name = "recipient_age")
    private Integer recipientAge;

    @Column
    private Integer budget;

    @Column(name = "preferred_categories", length = 500)
    private String preferredCategories; // JSON array string

    @Column(name = "additional_message", length = 1000)
    private String additionalMessage;

    @Column(name = "days_until_event")
    private Integer daysUntilEvent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RecommendationStatus status = RecommendationStatus.COMPLETED;

    public enum RecommendationStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
}
