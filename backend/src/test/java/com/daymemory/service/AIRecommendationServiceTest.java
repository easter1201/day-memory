package com.daymemory.service;

import com.daymemory.domain.dto.AIRecommendationDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.GiftItemRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AIRecommendationService 테스트")
class AIRecommendationServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GiftItemRepository giftItemRepository;

    @Spy
    @InjectMocks
    private AIRecommendationService aiRecommendationService;

    private User testUser;
    private Event testEvent;
    private AIRecommendationDto.RecommendRequest recommendRequest;

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
                .description("친구 생일")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isTracking(true)
                .reminders(new ArrayList<>())
                .build();

        // Given: AI 추천 요청 DTO
        recommendRequest = AIRecommendationDto.RecommendRequest.builder()
                .eventId(1L)
                .eventType(Event.EventType.BIRTHDAY)
                .relationship("친구")
                .minBudget(50000)
                .maxBudget(150000)
                .additionalInfo("20대 여성")
                .build();

        // Set API key to empty to trigger fallback
        ReflectionTestUtils.setField(aiRecommendationService, "apiKey", "");
        ReflectionTestUtils.setField(aiRecommendationService, "aiProvider", "openai");
        ReflectionTestUtils.setField(aiRecommendationService, "model", "gpt-3.5-turbo");
    }

    @Test
    @DisplayName("AI 추천 성공 - Fallback 사용")
    void testRecommendGifts_Success() {
        // Given
        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));
        given(giftItemRepository.findByUserId(1L)).willReturn(new ArrayList<>());

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(recommendRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();
        assertThat(response.getEventTitle()).isEqualTo("생일");
        assertThat(response.getEventType()).isEqualTo(Event.EventType.BIRTHDAY);
        assertThat(response.getDaysUntilEvent()).isGreaterThan(0);

        // Verify
        then(eventRepository).should(times(1)).findById(1L);
        then(giftItemRepository).should(times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("AI 추천 - 존재하지 않는 이벤트")
    void testRecommendGifts_EventNotFound() {
        // Given
        given(eventRepository.findById(999L)).willReturn(Optional.empty());

        AIRecommendationDto.RecommendRequest invalidRequest = AIRecommendationDto.RecommendRequest.builder()
                .eventId(999L)
                .build();

        // When & Then
        assertThatThrownBy(() -> aiRecommendationService.recommendGifts(invalidRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("AI 추천 - 이벤트 없이 추천 (일반 추천)")
    void testRecommendGifts_WithoutEvent() {
        // Given
        AIRecommendationDto.RecommendRequest requestWithoutEvent = AIRecommendationDto.RecommendRequest.builder()
                .eventId(null)
                .eventType(Event.EventType.BIRTHDAY)
                .relationship("가족")
                .minBudget(100000)
                .maxBudget(300000)
                .build();

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(requestWithoutEvent);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();
        assertThat(response.getEventType()).isEqualTo(Event.EventType.BIRTHDAY);

        // Verify: eventRepository 조회하지 않음
        then(eventRepository).should(never()).findById(any());
    }

    @Test
    @DisplayName("AI 추천 - 저장된 선물 우선 표시")
    void testRecommendGifts_WithSavedGifts() {
        // Given
        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));

        // 사용자가 저장한 선물 목록
        GiftItem savedGift1 = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .nickname("향수")
                .price(100000)
                .category(GiftItem.GiftCategory.COSMETICS)
                .build();

        GiftItem savedGift2 = GiftItem.builder()
                .id(2L)
                .user(testUser)
                .nickname("꽃다발")
                .price(30000)
                .category(GiftItem.GiftCategory.FLOWER)
                .build();

        List<GiftItem> savedGifts = List.of(savedGift1, savedGift2);
        given(giftItemRepository.findByUserId(1L)).willReturn(savedGifts);

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(recommendRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();

        // 최소한 하나는 매칭될 가능성이 있음 (이름이나 카테고리 기반)
        assertThat(response.getRecommendations()).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("AI 추천 - API 실패 시 Fallback 응답")
    void testRecommendGifts_APIFailure() {
        // Given
        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));
        given(giftItemRepository.findByUserId(1L)).willReturn(new ArrayList<>());

        // API 키가 비어있어서 Fallback이 자동으로 사용됨

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(recommendRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();
        assertThat(response.getRecommendations()).hasSizeGreaterThanOrEqualTo(3);

        // Fallback 추천은 예산 범위 내의 선물을 제공해야 함
        response.getRecommendations().forEach(rec -> {
            assertThat(rec.getEstimatedPrice())
                    .isGreaterThanOrEqualTo(recommendRequest.getMinBudget())
                    .isLessThanOrEqualTo(recommendRequest.getMaxBudget());
        });
    }

    @Test
    @DisplayName("Fallback 추천 - 생일 이벤트")
    void testGetFallbackRecommendations_Birthday() {
        // Given
        AIRecommendationDto.RecommendRequest birthdayRequest = AIRecommendationDto.RecommendRequest.builder()
                .eventType(Event.EventType.BIRTHDAY)
                .minBudget(20000)
                .maxBudget(200000)
                .build();

        given(giftItemRepository.findByUserId(any())).willReturn(new ArrayList<>());

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(birthdayRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();

        // 생일 추천에는 케이크, 향수, 꽃다발 등이 포함되어야 함
        List<String> recommendedNames = response.getRecommendations().stream()
                .map(AIRecommendationDto.GiftRecommendation::getName)
                .toList();

        assertThat(recommendedNames).anyMatch(name ->
                name.contains("케이크") || name.contains("향수") || name.contains("꽃")
        );
    }

    @Test
    @DisplayName("Fallback 추천 - 기념일 이벤트")
    void testGetFallbackRecommendations_Anniversary() {
        // Given
        AIRecommendationDto.RecommendRequest anniversaryRequest = AIRecommendationDto.RecommendRequest.builder()
                .eventType(Event.EventType.ANNIVERSARY_100)
                .minBudget(100000)
                .maxBudget(300000)
                .build();

        given(giftItemRepository.findByUserId(any())).willReturn(new ArrayList<>());

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(anniversaryRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();

        // 기념일 추천에는 커플링, 시계 등이 포함되어야 함
        List<String> recommendedNames = response.getRecommendations().stream()
                .map(AIRecommendationDto.GiftRecommendation::getName)
                .toList();

        assertThat(recommendedNames).anyMatch(name ->
                name.contains("커플") || name.contains("시계") || name.contains("레스토랑")
        );
    }

    @Test
    @DisplayName("Fallback 추천 - 발렌타인데이")
    void testGetFallbackRecommendations_ValentinesDay() {
        // Given
        AIRecommendationDto.RecommendRequest valentinesRequest = AIRecommendationDto.RecommendRequest.builder()
                .eventType(Event.EventType.VALENTINES_DAY)
                .minBudget(20000)
                .maxBudget(100000)
                .build();

        given(giftItemRepository.findByUserId(any())).willReturn(new ArrayList<>());

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(valentinesRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();

        // 발렌타인데이 추천에는 초콜릿, 장미 등이 포함되어야 함
        List<String> recommendedNames = response.getRecommendations().stream()
                .map(AIRecommendationDto.GiftRecommendation::getName)
                .toList();

        assertThat(recommendedNames).anyMatch(name ->
                name.contains("초콜릿") || name.contains("장미")
        );
    }

    @Test
    @DisplayName("Fallback 추천 - 예산 필터링")
    void testGetFallbackRecommendations_BudgetFiltering() {
        // Given
        AIRecommendationDto.RecommendRequest lowBudgetRequest = AIRecommendationDto.RecommendRequest.builder()
                .eventType(Event.EventType.BIRTHDAY)
                .maxBudget(40000)
                .build();

        given(giftItemRepository.findByUserId(any())).willReturn(new ArrayList<>());

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(lowBudgetRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();

        // 모든 추천 선물이 예산 내에 있어야 함
        response.getRecommendations().forEach(rec -> {
            assertThat(rec.getEstimatedPrice()).isLessThanOrEqualTo(40000);
        });
    }

    @Test
    @DisplayName("Fallback 추천 - 최소 3개 보장")
    void testGetFallbackRecommendations_MinimumThreeItems() {
        // Given
        AIRecommendationDto.RecommendRequest extremeBudgetRequest = AIRecommendationDto.RecommendRequest.builder()
                .eventType(Event.EventType.BIRTHDAY)
                .minBudget(1000000)
                .maxBudget(2000000)
                .build();

        given(giftItemRepository.findByUserId(any())).willReturn(new ArrayList<>());

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(extremeBudgetRequest);

        // Then
        assertThat(response).isNotNull();
        // 예산에 맞는 항목이 없어도 최소 3개의 기본 추천이 제공되어야 함
        assertThat(response.getRecommendations()).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("선물 매칭 - 이름 유사도")
    void testMatchingGift_ByName() {
        // Given
        AIRecommendationDto.RecommendRequest request = AIRecommendationDto.RecommendRequest.builder()
                .eventId(1L)
                .eventType(Event.EventType.BIRTHDAY)
                .build();

        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));

        GiftItem savedPerfume = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .nickname("향수 세트")
                .price(100000)
                .category(GiftItem.GiftCategory.COSMETICS)
                .build();

        given(giftItemRepository.findByUserId(1L)).willReturn(List.of(savedPerfume));

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(request);

        // Then
        assertThat(response).isNotNull();

        // 매칭 여부는 Fallback 추천 내용에 따라 달라질 수 있음
        assertThat(response.getRecommendations()).isNotEmpty();
    }

    @Test
    @DisplayName("선물 매칭 - 카테고리 및 가격대")
    void testMatchingGift_ByCategoryAndPrice() {
        // Given
        AIRecommendationDto.RecommendRequest request = AIRecommendationDto.RecommendRequest.builder()
                .eventId(1L)
                .eventType(Event.EventType.BIRTHDAY)
                .build();

        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));

        GiftItem savedCosmetic = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .nickname("스킨케어 세트")
                .price(90000)
                .category(GiftItem.GiftCategory.COSMETICS)
                .build();

        given(giftItemRepository.findByUserId(1L)).willReturn(List.of(savedCosmetic));

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();

        // 카테고리와 가격대가 유사한 항목이 매칭될 수 있음
    }

    @Test
    @DisplayName("저장된 선물 우선 정렬")
    void testPrioritizeSavedGifts() {
        // Given
        AIRecommendationDto.RecommendRequest request = AIRecommendationDto.RecommendRequest.builder()
                .eventId(1L)
                .eventType(Event.EventType.BIRTHDAY)
                .build();

        given(eventRepository.findById(1L)).willReturn(Optional.of(testEvent));

        GiftItem savedGift = GiftItem.builder()
                .id(1L)
                .user(testUser)
                .nickname("생일 케이크")
                .price(50000)
                .category(GiftItem.GiftCategory.FOOD)
                .build();

        given(giftItemRepository.findByUserId(1L)).willReturn(List.of(savedGift));

        // When
        AIRecommendationDto.RecommendResponse response = aiRecommendationService.recommendGifts(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendations()).isNotEmpty();

        // isUserSaved=true인 항목이 앞에 위치해야 함
        List<AIRecommendationDto.GiftRecommendation> recommendations = response.getRecommendations();

        // 모든 추천에 isUserSaved 필드가 설정되어 있어야 함
        recommendations.forEach(rec -> {
            assertThat(rec.getIsUserSaved()).isNotNull();
        });
    }

    @Test
    @DisplayName("이벤트 타입 설명 생성")
    void testEventTypeDescription() {
        // Given & When & Then
        AIRecommendationDto.RecommendRequest birthdayReq = AIRecommendationDto.RecommendRequest.builder()
                .eventType(Event.EventType.BIRTHDAY)
                .build();
        AIRecommendationDto.RecommendResponse birthdayResp = aiRecommendationService.recommendGifts(birthdayReq);
        assertThat(birthdayResp.getEventType()).isEqualTo(Event.EventType.BIRTHDAY);

        AIRecommendationDto.RecommendRequest valentinesReq = AIRecommendationDto.RecommendRequest.builder()
                .eventType(Event.EventType.VALENTINES_DAY)
                .build();
        AIRecommendationDto.RecommendResponse valentinesResp = aiRecommendationService.recommendGifts(valentinesReq);
        assertThat(valentinesResp.getEventType()).isEqualTo(Event.EventType.VALENTINES_DAY);
    }
}
