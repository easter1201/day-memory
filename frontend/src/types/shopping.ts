/**
 * 네이버 쇼핑 상품 정보
 */
export interface ShoppingProduct {
  title: string;              // 상품명 (HTML 태그 포함)
  link: string;                // 상품 상세 페이지 URL
  image: string;               // 상품 썸네일 이미지 URL
  lprice: string;              // 최저가 (문자열)
  hprice: string;              // 최고가 (문자열)
  mallName: string;            // 쇼핑몰 이름
  productId: string;           // 네이버 쇼핑 상품 ID
  productType: string;         // 상품 타입 (1: 일반, 2: 중고, 3: 단종, 4: 판매예정)
  brand?: string;              // 브랜드명
  maker?: string;              // 제조사명
  category1?: string;          // 카테고리 1단계
  category2?: string;          // 카테고리 2단계
  category3?: string;          // 카테고리 3단계
  category4?: string;          // 카테고리 4단계
}

/**
 * 상품 검색 쿼리 파라미터
 */
export interface ShoppingSearchParams {
  query: string;               // 검색어
  minPrice?: number;           // 최소 가격
  maxPrice?: number;           // 최대 가격
  display?: number;            // 검색 결과 개수 (기본 5)
}
