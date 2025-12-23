package com.daymemory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gift_items", indexes = {
    @Index(name = "idx_gift_user_id", columnList = "user_id"),
    @Index(name = "idx_gift_event_id", columnList = "event_id"),
    @Index(name = "idx_gift_user_purchased", columnList = "user_id, is_purchased"),
    @Index(name = "idx_gift_category", columnList = "category"),
    @Index(name = "idx_gift_user_category", columnList = "user_id, category")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GiftItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column
    private Integer price;

    @Column(name = "estimated_price")
    private Integer estimatedPrice;

    @Column
    private Integer budget;

    @Column
    private String url;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GiftCategory category = GiftCategory.OTHER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPurchased = false;

    public enum GiftCategory {
        FLOWER,         // 꽃
        JEWELRY,        // 주얼리
        COSMETICS,      // 화장품
        FASHION,        // 패션
        ELECTRONICS,    // 전자기기
        FOOD,           // 음식/디저트
        EXPERIENCE,     // 체험/이벤트
        BOOK,           // 책
        HOBBY,          // 취미용품
        OTHER           // 기타
    }

    public void update(String name, String description, Integer price, String url, GiftCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.url = url;
        this.category = category;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void markAsPurchased() {
        this.isPurchased = true;
    }

    public void markAsNotPurchased() {
        this.isPurchased = false;
    }
}
