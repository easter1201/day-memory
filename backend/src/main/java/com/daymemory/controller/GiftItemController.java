package com.daymemory.controller;

import com.daymemory.domain.dto.GiftItemDto;
import com.daymemory.service.GiftItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<GiftItemDto.Response>> getGiftItems(@RequestParam Long userId) {
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
}
