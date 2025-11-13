package com.daymemory.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ShoppingDto {

    /**
     * 네이버 쇼핑 API 검색 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        private String query;           // 검색어
        private Integer display;        // 검색 결과 출력 건수 (기본 10, 최대 100)
        private Integer start;          // 검색 시작 위치 (기본 1, 최대 1000)
        private String sort;            // 정렬 옵션 (sim: 정확도순, date: 날짜순, asc: 가격오름차순, dsc: 가격내림차순)
    }

    /**
     * 네이버 쇼핑 API 검색 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResponse {
        @JsonProperty("lastBuildDate")
        private String lastBuildDate;   // 검색 결과를 생성한 시간

        private Integer total;          // 총 검색 결과 개수
        private Integer start;          // 검색 시작 위치
        private Integer display;        // 한 번에 표시할 검색 결과 개수
        private List<ProductDto> items; // 개별 검색 결과
    }

    /**
     * 상품 정보 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private String title;           // 상품명 (HTML 태그 제거 필요)
        private String link;            // 상품 상세 페이지 URL
        private String image;           // 상품 썸네일 이미지 URL

        @JsonProperty("lprice")
        private String lprice;          // 최저가 (문자열로 전달됨)

        @JsonProperty("hprice")
        private String hprice;          // 최고가 (문자열로 전달됨)

        @JsonProperty("mallName")
        private String mallName;        // 쇼핑몰 이름

        @JsonProperty("productId")
        private String productId;       // 네이버 쇼핑 상품 ID

        @JsonProperty("productType")
        private String productType;     // 상품 타입 (1: 일반상품, 2: 중고상품, 3: 단종상품, 4: 판매예정상품)

        private String brand;           // 브랜드명
        private String maker;           // 제조사명
        private String category1;       // 카테고리 1단계
        private String category2;       // 카테고리 2단계
        private String category3;       // 카테고리 3단계
        private String category4;       // 카테고리 4단계

        /**
         * HTML 태그가 포함된 title을 정리하여 반환
         */
        public String getCleanTitle() {
            if (title == null) {
                return "";
            }
            return title.replaceAll("<[^>]*>", "").trim();
        }

        /**
         * 최저가를 숫자로 변환하여 반환
         */
        public Integer getLpriceAsInt() {
            try {
                return lprice != null ? Integer.parseInt(lprice) : 0;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        /**
         * 최고가를 숫자로 변환하여 반환
         */
        public Integer getHpriceAsInt() {
            try {
                return hprice != null ? Integer.parseInt(hprice) : 0;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        /**
         * 가격을 포맷팅하여 반환 (예: 15000 -> "15,000원")
         */
        public String getFormattedLprice() {
            return String.format("%,d원", getLpriceAsInt());
        }
    }
}
