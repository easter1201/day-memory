package com.daymemory.service;

import com.daymemory.domain.dto.ShoppingDto;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverShoppingService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private static final String NAVER_SHOPPING_API_URL = "https://openapi.naver.com/v1/search/shop.json";
    private final ObjectMapper objectMapper;

    /**
     * 네이버 쇼핑 API를 사용하여 상품 검색
     *
     * @param query 검색어 (예: "조향 키트")
     * @param display 검색 결과 개수 (기본 5, 최대 100)
     * @return 검색된 상품 목록
     */
    public List<ShoppingDto.ProductDto> searchProducts(String query, Integer display) {
        if (query == null || query.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 기본값 설정
        int displayCount = (display != null && display > 0 && display <= 100) ? display : 5;

        try {
            // API 호출
            String apiUrl = buildApiUrl(query, displayCount);
            String responseBody = callNaverApi(apiUrl);

            // JSON 파싱
            ShoppingDto.SearchResponse response = objectMapper.readValue(
                    responseBody, ShoppingDto.SearchResponse.class
            );

            log.info("Naver Shopping API search completed. Query: {}, Results: {}",
                    query, response.getItems().size());

            return response.getItems();

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to search products from Naver Shopping API. Query: {}", query, e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    /**
     * 가격 범위를 포함한 상품 검색
     *
     * @param query 검색어
     * @param minPrice 최소 가격 (null 가능)
     * @param maxPrice 최대 가격 (null 가능)
     * @param display 검색 결과 개수
     * @return 가격 범위에 맞는 상품 목록
     */
    public List<ShoppingDto.ProductDto> searchProductsWithPriceRange(
            String query, Integer minPrice, Integer maxPrice, Integer display) {

        List<ShoppingDto.ProductDto> products = searchProducts(query, display);

        // 가격 필터링
        if (minPrice != null || maxPrice != null) {
            return products.stream()
                    .filter(product -> {
                        int price = product.getLpriceAsInt();
                        boolean matchMin = minPrice == null || price >= minPrice;
                        boolean matchMax = maxPrice == null || price <= maxPrice;
                        return matchMin && matchMax;
                    })
                    .limit(display != null ? display : 5)
                    .toList();
        }

        return products;
    }

    /**
     * API URL 생성
     */
    private String buildApiUrl(String query, int display) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return String.format("%s?query=%s&display=%d&sort=sim",
                NAVER_SHOPPING_API_URL, encodedQuery, display);
    }

    /**
     * 네이버 API 호출
     */
    private String callNaverApi(String apiUrl) throws Exception {
        // 디버그: Client ID와 Secret이 제대로 로딩되었는지 확인
        log.info("=== Naver API Request ===");
        log.info("URL: {}", apiUrl);
        log.info("Client ID: {}", clientId != null ? (clientId.isEmpty() ? "EMPTY" : "Loaded (length: " + clientId.length() + ")") : "NULL");
        log.info("Client Secret: {}", clientSecret != null ? (clientSecret.isEmpty() ? "EMPTY" : "Loaded (length: " + clientSecret.length() + ")") : "NULL");

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Naver-Client-Id", clientId);
            connection.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            int responseCode = connection.getResponseCode();
            log.info("Response Code: {}", responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readResponse(connection);
                log.info("Success! Response length: {} characters", response.length());
                return response;
            } else {
                log.error("=== Naver API ERROR ===");
                log.error("Response code: {}", responseCode);
                String errorResponse = readErrorResponse(connection);
                log.error("Error response: {}", errorResponse);
                throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
            }

        } finally {
            connection.disconnect();
        }
    }

    /**
     * 성공 응답 읽기
     */
    private String readResponse(HttpURLConnection connection) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * 에러 응답 읽기
     */
    private String readErrorResponse(HttpURLConnection connection) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
