package com.daymemory.domain.dto;

import com.daymemory.domain.entity.GiftItem;
import jakarta.validation.constraints.*;
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

        @NotBlank(message = "선물 이름은 필수입니다.")
        @Size(max = 100, message = "선물 이름은 100자 이하여야 합니다.")
        private String name;

        @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
        private String description;

        @Positive(message = "가격은 양수여야 합니다.")
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
        private String imageUrl;
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
                    .imageUrl(giftItem.getImageUrl())
                    .category(giftItem.getCategory())
                    .isPurchased(giftItem.getIsPurchased())
                    .build();
        }
    }
}
