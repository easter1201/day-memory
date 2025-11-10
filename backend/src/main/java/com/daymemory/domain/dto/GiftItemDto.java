package com.daymemory.domain.dto;

import com.daymemory.domain.entity.GiftItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GiftItemDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long eventId;
        private String name;
        private String description;
        private Integer price;
        private String url;
        private GiftItem.GiftCategory category;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long eventId;
        private String name;
        private String description;
        private Integer price;
        private String url;
        private GiftItem.GiftCategory category;
        private Boolean isPurchased;

        public static Response from(GiftItem giftItem) {
            return Response.builder()
                    .id(giftItem.getId())
                    .eventId(giftItem.getEvent() != null ? giftItem.getEvent().getId() : null)
                    .name(giftItem.getName())
                    .description(giftItem.getDescription())
                    .price(giftItem.getPrice())
                    .url(giftItem.getUrl())
                    .category(giftItem.getCategory())
                    .isPurchased(giftItem.getIsPurchased())
                    .build();
        }
    }
}
