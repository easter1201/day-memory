package com.daymemory.domain.dto;

import com.daymemory.domain.entity.GiftItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GiftItemDto {

    @Schema(description = "선물 아이템 생성/수정 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @Schema(description = "연결할 이벤트 ID", example = "1")
        private Long eventId;

        @Schema(description = "선물 이름", example = "에어팟 프로", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "선물 이름은 필수입니다.")
        @Size(max = 100, message = "선물 이름은 100자 이하여야 합니다.")
        private String name;

        @Schema(description = "선물 설명", example = "2세대, 화이트 컬러")
        @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
        private String description;

        @Schema(description = "선물 가격", example = "350000")
        @Positive(message = "가격은 양수여야 합니다.")
        private Integer price;

        @Schema(description = "구매 URL", example = "https://www.apple.com/kr/airpods-pro")
        private String url;

        @Schema(description = "선물 카테고리 (ELECTRONICS, FASHION, FOOD, BOOK, COSMETICS, FLOWER, JEWELRY, TOY, OTHER)", example = "ELECTRONICS")
        private GiftItem.GiftCategory category;
    }

    @Schema(description = "선물 아이템 응답")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @Schema(description = "선물 ID", example = "1")
        private Long id;

        @Schema(description = "연결된 이벤트 ID", example = "1")
        private Long eventId;

        @Schema(description = "선물 이름", example = "에어팟 프로")
        private String name;

        @Schema(description = "선물 설명", example = "2세대, 화이트 컬러")
        private String description;

        @Schema(description = "선물 가격", example = "350000")
        private Integer price;

        @Schema(description = "구매 URL", example = "https://www.apple.com/kr/airpods-pro")
        private String url;

        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
        private String imageUrl;

        @Schema(description = "선물 카테고리", example = "ELECTRONICS")
        private GiftItem.GiftCategory category;

        @Schema(description = "구매 완료 여부", example = "false")
        private Boolean isPurchased;

        @Schema(description = "생성일시", example = "2025-11-13T10:30:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2025-11-13T10:30:00")
        private String updatedAt;

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
                    .createdAt(giftItem.getCreatedAt() != null ? giftItem.getCreatedAt().toString() : null)
                    .updatedAt(giftItem.getUpdatedAt() != null ? giftItem.getUpdatedAt().toString() : null)
                    .build();
        }
    }
}
