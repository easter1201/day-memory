package com.daymemory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommended_gift_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecommendedGiftItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false)
    private AIRecommendation recommendation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saved_gift_id")
    private GiftItem savedGift; // 사용자가 선물 탭에 저장한 경우

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GiftItem.GiftCategory category;

    @Column(name = "estimated_price", nullable = false)
    private Integer estimatedPrice;

    @Column(length = 1000)
    private String reason;

    @Column(name = "purchase_link")
    private String purchaseLink;

    public void setSavedGift(GiftItem savedGift) {
        this.savedGift = savedGift;
    }
}
