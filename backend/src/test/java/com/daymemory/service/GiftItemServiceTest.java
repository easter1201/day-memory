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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GiftItemService 테스트")
class GiftItemServiceTest {

    @Mock
    private GiftItemRepository giftItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private GiftItemService giftItemService;

    private User testUser;
    private Event testEvent;
    private GiftItem testGiftItem;
    private GiftItemDto.Request createRequest;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 사용자 설정
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("테스트 사용자")
                .build();

        // Given: 테스트용 이벤트 설정
        testEvent = Event.builder()
                .id(1L)
                .user(testUser)
                .title("생일")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .reminders(new ArrayList<>())
                .build();

        // Given: 테스트용 선물 설정
        testGiftItem = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .event(testEvent)
                .nickname("향수")
                .description("고급 향수")
                .price(100000)
                .url("https://example.com/perfume")
                .category(GiftItem.GiftCategory.COSMETICS)
                .isPurchased(false)
                .build();

        // Given: 선물 생성 요청 DTO
        createRequest = GiftItemDto.Request.builder()
                .eventId(1L)
                .nickname("향수")
                .description("고급 향수")
                .price(100000)
                .url("https://example.com/perfume")
                .category(GiftItem.GiftCategory.COSMETICS)
                .build();
    }

    @Test
    @DisplayName("선물 생성 성공")
    void testCreateGift_Success() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));
        given(giftItemRepository.save(any(GiftItem.class))).willReturn(testGiftItem);

        // When
        GiftItemDto.Response response = giftItemService.createGiftItem(1L, createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("향수");
        assertThat(response.getPrice()).isEqualTo(100000);
        assertThat(response.getCategory()).isEqualTo(GiftItem.GiftCategory.COSMETICS);

        // Verify
        then(userRepository).should(times(1)).findById(1L);
        then(eventRepository).should(times(1)).findById(1L);
        then(giftItemRepository).should(times(1)).save(any(GiftItem.class));
    }

    @Test
    @DisplayName("선물 생성 - 존재하지 않는 이벤트")
    void testCreateGift_EventNotFound() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.findById(999L)).willReturn(Optional.empty());

        GiftItemDto.Request requestWithInvalidEvent = GiftItemDto.Request.builder()
                .eventId(999L)
                .nickname("선물")
                .build();

        // When & Then
        assertThatThrownBy(() -> giftItemService.createGiftItem(1L, requestWithInvalidEvent))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EVENT_NOT_FOUND);

        // Verify
        then(giftItemRepository).should(never()).save(any(GiftItem.class));
    }

    @Test
    @DisplayName("선물 생성 - 이벤트 없이 생성 (일반 선물)")
    void testCreateGift_WithoutEvent() {
        // Given
        GiftItemDto.Request requestWithoutEvent = GiftItemDto.Request.builder()
                .eventId(null)
                .nickname("일반 선물")
                .description("이벤트와 관련없는 선물")
                .price(50000)
                .category(GiftItem.GiftCategory.OTHER)
                .build();

        GiftItem giftWithoutEvent = GiftItem.builder()
                .id(2L)
                .user(testUser)
                .event(null)
                .nickname("일반 선물")
                .description("이벤트와 관련없는 선물")
                .price(50000)
                .category(GiftItem.GiftCategory.OTHER)
                .isPurchased(false)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(giftItemRepository.save(any(GiftItem.class))).willReturn(giftWithoutEvent);

        // When
        GiftItemDto.Response response = giftItemService.createGiftItem(1L, requestWithoutEvent);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("일반 선물");

        // Verify
        then(eventRepository).should(never()).findById(any());
        then(giftItemRepository).should(times(1)).save(any(GiftItem.class));
    }

    @Test
    @DisplayName("선물 조회 성공")
    void testGetGift_Success() {
        // Given
        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));

        // When
        GiftItemDto.Response response = GiftItemDto.Response.from(testGiftItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("향수");
    }

    @Test
    @DisplayName("사용자별 선물 목록 조회")
    void testGetGiftItemsByUser() {
        // Given
        List<GiftItem> giftItems = List.of(testGiftItem);
        given(giftItemRepository.findByUserId(1L)).willReturn(giftItems);

        // When
        List<GiftItemDto.Response> responses = giftItemService.getGiftItemsByUser(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("향수");

        // Verify
        then(giftItemRepository).should(times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("이벤트별 선물 목록 조회")
    void testGetGiftItemsByEvent() {
        // Given
        List<GiftItem> giftItems = List.of(testGiftItem);
        given(giftItemRepository.findByEventId(1L)).willReturn(giftItems);

        // When
        List<GiftItemDto.Response> responses = giftItemService.getGiftItemsByEvent(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("향수");

        // Verify
        then(giftItemRepository).should(times(1)).findByEventId(1L);
    }

    @Test
    @DisplayName("구매 상태 토글 - 미구매 -> 구매")
    void testPurchaseGift_Toggle_ToPurchased() {
        // Given
        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));

        // When
        GiftItemDto.Response response = giftItemService.togglePurchaseStatus(1L);

        // Then
        assertThat(response).isNotNull();

        // Verify
        then(giftItemRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("구매 상태 토글 - 구매 -> 미구매")
    void testPurchaseGift_Toggle_ToUnpurchased() {
        // Given
        testGiftItem = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .nickname("향수")
                .isPurchased(true)
                .build();

        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));

        // When
        GiftItemDto.Response response = giftItemService.togglePurchaseStatus(1L);

        // Then
        assertThat(response).isNotNull();

        // Verify
        then(giftItemRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("선물 검색")
    void testSearchGifts() {
        // Given
        String keyword = "향수";
        List<GiftItem> searchResults = List.of(testGiftItem);
        given(giftItemRepository.searchByKeyword(1L, keyword)).willReturn(searchResults);

        // When
        List<GiftItemDto.Response> responses = giftItemService.searchGiftItems(1L, keyword);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).contains("향수");

        // Verify
        then(giftItemRepository).should(times(1)).searchByKeyword(1L, keyword);
    }

    @Test
    @DisplayName("선물 삭제 성공")
    void testDeleteGift_Success() {
        // Given
        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));
        willDoNothing().given(giftItemRepository).delete(testGiftItem);

        // When
        giftItemService.deleteGiftItem(1L);

        // Then
        // Verify
        then(giftItemRepository).should(times(1)).findById(1L);
        then(giftItemRepository).should(times(1)).delete(testGiftItem);
    }

    @Test
    @DisplayName("선물 삭제 - 존재하지 않는 선물")
    void testDeleteGift_NotFound() {
        // Given
        given(giftItemRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> giftItemService.deleteGiftItem(999L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.GIFT_NOT_FOUND);

        // Verify
        then(giftItemRepository).should(never()).delete(any(GiftItem.class));
    }

    @Test
    @DisplayName("미구매 선물 목록 조회")
    void testGetUnpurchasedGiftItems() {
        // Given
        List<GiftItem> unpurchasedGifts = List.of(testGiftItem);
        given(giftItemRepository.findByUserIdAndIsPurchasedFalse(1L)).willReturn(unpurchasedGifts);

        // When
        List<GiftItemDto.Response> responses = giftItemService.getUnpurchasedGiftItems(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getIsPurchased()).isFalse();

        // Verify
        then(giftItemRepository).should(times(1)).findByUserIdAndIsPurchasedFalse(1L);
    }

    @Test
    @DisplayName("카테고리별 선물 조회")
    void testGetGiftItemsByCategory() {
        // Given
        List<GiftItem> cosmeticsGifts = List.of(testGiftItem);
        given(giftItemRepository.findByUserIdAndCategory(1L, GiftItem.GiftCategory.COSMETICS))
                .willReturn(cosmeticsGifts);

        // When
        List<GiftItemDto.Response> responses = giftItemService.getGiftItemsByCategory(
                1L, GiftItem.GiftCategory.COSMETICS);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCategory()).isEqualTo(GiftItem.GiftCategory.COSMETICS);

        // Verify
        then(giftItemRepository).should(times(1))
                .findByUserIdAndCategory(1L, GiftItem.GiftCategory.COSMETICS);
    }

    @Test
    @DisplayName("선물 이미지 업로드 성공")
    void testUploadGiftImage_Success() {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        String imageUrl = "https://storage.example.com/images/gift123.jpg";

        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));
        willDoNothing().given(fileStorageService).validateImageFile(mockFile);
        given(fileStorageService.uploadFile(mockFile)).willReturn(imageUrl);

        // When
        GiftItemDto.Response response = giftItemService.uploadGiftImage(1L, mockFile);

        // Then
        assertThat(response).isNotNull();

        // Verify
        then(giftItemRepository).should(times(1)).findById(1L);
        then(fileStorageService).should(times(1)).validateImageFile(mockFile);
        then(fileStorageService).should(times(1)).uploadFile(mockFile);
    }

    @Test
    @DisplayName("선물 이미지 업로드 - 기존 이미지 교체")
    void testUploadGiftImage_ReplaceExisting() {
        // Given
        String oldImageUrl = "https://storage.example.com/images/old.jpg";
        String newImageUrl = "https://storage.example.com/images/new.jpg";
        testGiftItem = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .nickname("향수")
                .imageUrl(oldImageUrl)
                .build();

        MultipartFile mockFile = mock(MultipartFile.class);

        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));
        willDoNothing().given(fileStorageService).validateImageFile(mockFile);
        willDoNothing().given(fileStorageService).deleteFile(oldImageUrl);
        given(fileStorageService.uploadFile(mockFile)).willReturn(newImageUrl);

        // When
        GiftItemDto.Response response = giftItemService.uploadGiftImage(1L, mockFile);

        // Then
        assertThat(response).isNotNull();

        // Verify: 기존 이미지 삭제 확인
        then(fileStorageService).should(times(1)).deleteFile(oldImageUrl);
        then(fileStorageService).should(times(1)).uploadFile(mockFile);
    }

    @Test
    @DisplayName("선물 이미지 삭제 성공")
    void testDeleteGiftImage_Success() {
        // Given
        String imageUrl = "https://storage.example.com/images/gift123.jpg";
        testGiftItem = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .nickname("향수")
                .imageUrl(imageUrl)
                .build();

        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));
        willDoNothing().given(fileStorageService).deleteFile(imageUrl);

        // When
        giftItemService.deleteGiftImage(1L);

        // Then
        // Verify
        then(giftItemRepository).should(times(1)).findById(1L);
        then(fileStorageService).should(times(1)).deleteFile(imageUrl);
    }

    @Test
    @DisplayName("선물 수정 성공")
    void testUpdateGiftItem_Success() {
        // Given
        GiftItemDto.Request updateRequest = GiftItemDto.Request.builder()
                .nickname("수정된 향수")
                .description("새로운 설명")
                .price(150000)
                .url("https://example.com/new-perfume")
                .category(GiftItem.GiftCategory.COSMETICS)
                .build();

        given(giftItemRepository.findById(1L)).willReturn(Optional.of(testGiftItem));

        // When
        GiftItemDto.Response response = giftItemService.updateGiftItem(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();

        // Verify
        then(giftItemRepository).should(times(1)).findById(1L);
    }

    @Test
    @DisplayName("선물 수정 - 존재하지 않는 선물")
    void testUpdateGiftItem_NotFound() {
        // Given
        GiftItemDto.Request updateRequest = GiftItemDto.Request.builder()
                .nickname("수정된 선물")
                .build();

        given(giftItemRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> giftItemService.updateGiftItem(999L, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.GIFT_NOT_FOUND);
    }
}
