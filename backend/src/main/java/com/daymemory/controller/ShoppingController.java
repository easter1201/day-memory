package com.daymemory.controller;

import com.daymemory.domain.dto.ShoppingDto;
import com.daymemory.service.NaverShoppingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Shopping", description = "쇼핑 상품 검색 API - 네이버 쇼핑 API를 활용하여 선물 관련 상품을 검색합니다.")
@RestController
@RequestMapping("/api/shopping")
@RequiredArgsConstructor
public class ShoppingController {

    private final NaverShoppingService naverShoppingService;

    /**
     * 네이버 API 설정 확인용 (개발용)
     */
    @GetMapping("/config-check")
    public ResponseEntity<String> checkConfig() {
        return ResponseEntity.ok("Naver Shopping API Configuration Check Endpoint");
    }

    /**
     * 네이버 쇼핑 API로 상품 검색
     * GET /api/shopping/search?query={검색어}&display={개수}
     * GET /api/shopping/search?query={검색어}&minPrice={최소가격}&maxPrice={최대가격}&display={개수}
     */
    @Operation(summary = "상품 검색", description = "네이버 쇼핑 API를 사용하여 상품을 검색합니다. 검색어와 가격 범위를 지정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShoppingDto.ProductDto.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (검색어 누락 등)",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "외부 API 호출 실패",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<ShoppingDto.ProductDto>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "5") Integer display) {

        List<ShoppingDto.ProductDto> products;

        if (minPrice != null || maxPrice != null) {
            products = naverShoppingService.searchProductsWithPriceRange(query, minPrice, maxPrice, display);
        } else {
            products = naverShoppingService.searchProducts(query, display);
        }

        return ResponseEntity.ok(products);
    }
}
