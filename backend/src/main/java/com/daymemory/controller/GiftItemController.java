package com.daymemory.controller;

import com.daymemory.domain.dto.GiftItemDto;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.security.SecurityUtils;
import com.daymemory.service.GiftItemService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Gift Item", description = "선물 아이템 관리 API - 선물 목록을 관리하고 구매 상태를 추적합니다.")
@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftItemController {

    private final GiftItemService giftItemService;

    @Operation(summary = "선물 아이템 생성", description = "새로운 선물 아이템을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "선물 생성 성공",
                    content = @Content(schema = @Schema(implementation = GiftItemDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<GiftItemDto.Response> createGiftItem(
            @RequestBody GiftItemDto.Request request) {
        Long userId = SecurityUtils.getCurrentUserId();
        GiftItemDto.Response response = giftItemService.createGiftItem(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "선물 목록 조회", description = "사용자의 선물 목록을 조회합니다. 구매 상태 또는 카테고리로 필터링할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "선물 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GiftItemDto.Response.class)))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<GiftItemDto.Response>> getGiftItems(
            @RequestParam(required = false) Boolean isPurchased,
            @RequestParam(required = false) GiftItem.GiftCategory category) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 구매 상태와 카테고리 모두로 필터링
        if (isPurchased != null && category != null) {
            return ResponseEntity.ok(giftItemService.getGiftItemsByPurchaseStatusAndCategory(userId, isPurchased, category));
        }

        // 구매 상태로만 필터링
        if (isPurchased != null) {
            if (isPurchased) {
                return ResponseEntity.ok(giftItemService.getPurchasedGiftItems(userId));
            } else {
                return ResponseEntity.ok(giftItemService.getUnpurchasedGiftItems(userId));
            }
        }

        // 카테고리별 조회
        if (category != null) {
            return ResponseEntity.ok(giftItemService.getGiftItemsByCategory(userId, category));
        }

        // 전체 조회
        List<GiftItemDto.Response> giftItems = giftItemService.getGiftItemsByUser(userId);
        return ResponseEntity.ok(giftItems);
    }

    @Operation(summary = "선물 단건 조회", description = "선물 ID로 선물 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "선물 조회 성공",
                    content = @Content(schema = @Schema(implementation = GiftItemDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "선물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/{giftId}")
    public ResponseEntity<GiftItemDto.Response> getGiftItem(@PathVariable Long giftId) {
        GiftItemDto.Response response = giftItemService.getGiftItemById(giftId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트별 선물 조회", description = "특정 이벤트와 연결된 선물 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "선물 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GiftItemDto.Response.class)))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<GiftItemDto.Response>> getGiftItemsByEvent(@PathVariable Long eventId) {
        List<GiftItemDto.Response> giftItems = giftItemService.getGiftItemsByEvent(eventId);
        return ResponseEntity.ok(giftItems);
    }

    @Operation(summary = "선물 정보 수정", description = "기존 선물 아이템의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "선물 수정 성공",
                    content = @Content(schema = @Schema(implementation = GiftItemDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "선물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PutMapping("/{giftId}")
    public ResponseEntity<GiftItemDto.Response> updateGiftItem(
            @PathVariable Long giftId,
            @RequestBody GiftItemDto.Request request) {
        GiftItemDto.Response response = giftItemService.updateGiftItem(giftId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "구매 상태 토글", description = "선물의 구매 상태를 변경합니다. (미구매 <-> 구매 완료)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "구매 상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = GiftItemDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "선물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PatchMapping("/{giftId}/purchase")
    public ResponseEntity<GiftItemDto.Response> togglePurchaseStatus(@PathVariable Long giftId) {
        GiftItemDto.Response response = giftItemService.togglePurchaseStatus(giftId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "선물 삭제", description = "선물 아이템을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "선물 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "선물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @DeleteMapping("/{giftId}")
    public ResponseEntity<Void> deleteGiftItem(@PathVariable Long giftId) {
        giftItemService.deleteGiftItem(giftId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 선물 검색
     * GET /api/gifts/search?userId={userId}&keyword={keyword}
     */
    @Operation(summary = "선물 검색", description = "키워드로 선물을 검색합니다. 선물 이름, 설명, 메모 등에서 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GiftItemDto.Response.class)))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<GiftItemDto.Response>> searchGiftItems(
            @RequestParam String keyword) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GiftItemDto.Response> giftItems = giftItemService.searchGiftItems(userId, keyword);
        return ResponseEntity.ok(giftItems);
    }

    /**
     * 선물 이미지 업로드
     * POST /api/gifts/{giftId}/image
     */
    @Operation(summary = "선물 이미지 업로드", description = "선물에 이미지를 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
                    content = @Content(schema = @Schema(implementation = GiftItemDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기 초과",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "선물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping("/{giftId}/image")
    public ResponseEntity<GiftItemDto.Response> uploadGiftImage(
            @PathVariable Long giftId,
            @RequestParam("file") MultipartFile file) {
        GiftItemDto.Response response = giftItemService.uploadGiftImage(giftId, file);
        return ResponseEntity.ok(response);
    }

    /**
     * 선물 이미지 삭제
     * DELETE /api/gifts/{giftId}/image
     */
    @Operation(summary = "선물 이미지 삭제", description = "선물에서 이미지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "이미지 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "선물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @DeleteMapping("/{giftId}/image")
    public ResponseEntity<Void> deleteGiftImage(@PathVariable Long giftId) {
        giftItemService.deleteGiftImage(giftId);
        return ResponseEntity.noContent().build();
    }
}
