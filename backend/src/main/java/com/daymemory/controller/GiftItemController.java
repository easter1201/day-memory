package com.daymemory.controller;

import com.daymemory.domain.dto.GiftItemDto;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.service.GiftItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftItemController {

    private final GiftItemService giftItemService;

    @PostMapping
    public ResponseEntity<GiftItemDto.Response> createGiftItem(
            @RequestParam Long userId,
            @RequestBody GiftItemDto.Request request) {
        GiftItemDto.Response response = giftItemService.createGiftItem(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GiftItemDto.Response>> getGiftItems(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean purchased,
            @RequestParam(required = false) GiftItem.GiftCategory category) {

        // 미구매 선물 조회
        if (purchased != null && !purchased) {
            return ResponseEntity.ok(giftItemService.getUnpurchasedGiftItems(userId));
        }

        // 카테고리별 조회
        if (category != null) {
            return ResponseEntity.ok(giftItemService.getGiftItemsByCategory(userId, category));
        }

        // 전체 조회
        List<GiftItemDto.Response> giftItems = giftItemService.getGiftItemsByUser(userId);
        return ResponseEntity.ok(giftItems);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<GiftItemDto.Response>> getGiftItemsByEvent(@PathVariable Long eventId) {
        List<GiftItemDto.Response> giftItems = giftItemService.getGiftItemsByEvent(eventId);
        return ResponseEntity.ok(giftItems);
    }

    @PutMapping("/{giftId}")
    public ResponseEntity<GiftItemDto.Response> updateGiftItem(
            @PathVariable Long giftId,
            @RequestBody GiftItemDto.Request request) {
        GiftItemDto.Response response = giftItemService.updateGiftItem(giftId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{giftId}/purchase")
    public ResponseEntity<GiftItemDto.Response> togglePurchaseStatus(@PathVariable Long giftId) {
        GiftItemDto.Response response = giftItemService.togglePurchaseStatus(giftId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{giftId}")
    public ResponseEntity<Void> deleteGiftItem(@PathVariable Long giftId) {
        giftItemService.deleteGiftItem(giftId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 선물 검색
     * GET /api/gifts/search?userId={userId}&keyword={keyword}
     */
    @GetMapping("/search")
    public ResponseEntity<List<GiftItemDto.Response>> searchGiftItems(
            @RequestParam Long userId,
            @RequestParam String keyword) {
        List<GiftItemDto.Response> giftItems = giftItemService.searchGiftItems(userId, keyword);
        return ResponseEntity.ok(giftItems);
    }

    /**
     * 선물 이미지 업로드
     * POST /api/gifts/{giftId}/image
     */
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
    @DeleteMapping("/{giftId}/image")
    public ResponseEntity<Void> deleteGiftImage(@PathVariable Long giftId) {
        giftItemService.deleteGiftImage(giftId);
        return ResponseEntity.noContent().build();
    }
}
