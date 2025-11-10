package com.daymemory.service;

import com.daymemory.domain.dto.GiftItemDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.GiftItemRepository;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GiftItemService {

    private final GiftItemRepository giftItemRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public GiftItemDto.Response createGiftItem(Long userId, GiftItemDto.Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Event event = null;
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        }

        GiftItem giftItem = GiftItem.builder()
                .user(user)
                .event(event)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .url(request.getUrl())
                .category(request.getCategory() != null ? request.getCategory() : GiftItem.GiftCategory.OTHER)
                .build();

        GiftItem savedGiftItem = giftItemRepository.save(giftItem);
        return GiftItemDto.Response.from(savedGiftItem);
    }

    public List<GiftItemDto.Response> getGiftItemsByUser(Long userId) {
        List<GiftItem> giftItems = giftItemRepository.findByUserId(userId);
        return giftItems.stream()
                .map(GiftItemDto.Response::from)
                .collect(Collectors.toList());
    }

    public List<GiftItemDto.Response> getGiftItemsByEvent(Long eventId) {
        List<GiftItem> giftItems = giftItemRepository.findByEventId(eventId);
        return giftItems.stream()
                .map(GiftItemDto.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public GiftItemDto.Response updateGiftItem(Long giftId, GiftItemDto.Request request) {
        GiftItem giftItem = giftItemRepository.findById(giftId)
                .orElseThrow(() -> new CustomException(ErrorCode.GIFT_NOT_FOUND));

        giftItem.update(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getUrl(),
                request.getCategory()
        );

        return GiftItemDto.Response.from(giftItem);
    }

    @Transactional
    public GiftItemDto.Response togglePurchaseStatus(Long giftId) {
        GiftItem giftItem = giftItemRepository.findById(giftId)
                .orElseThrow(() -> new CustomException(ErrorCode.GIFT_NOT_FOUND));

        if (giftItem.getIsPurchased()) {
            giftItem.markAsNotPurchased();
        } else {
            giftItem.markAsPurchased();
        }

        return GiftItemDto.Response.from(giftItem);
    }

    @Transactional
    public void deleteGiftItem(Long giftId) {
        GiftItem giftItem = giftItemRepository.findById(giftId)
                .orElseThrow(() -> new CustomException(ErrorCode.GIFT_NOT_FOUND));
        giftItemRepository.delete(giftItem);
    }
}
