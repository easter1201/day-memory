package com.daymemory.service;

import com.daymemory.domain.dto.AIRecommendationDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.GiftItemRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIRecommendationService {

    private final EventRepository eventRepository;
    private final GiftItemRepository giftItemRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.provider:openai}")
    private String aiProvider;

    @Value("${ai.model:gpt-3.5-turbo}")
    private String model;

    /**
     * AI 기반 선물 추천
     */
    public AIRecommendationDto.RecommendResponse recommendGifts(AIRecommendationDto.RecommendRequest request) {
        // 이벤트 정보 조회
        Event event = null;
        Event.EventType eventType = request.getEventType();
        String eventTitle = "특별한 날";
        int daysUntilEvent = 0;
        Long userId = null;

        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
            eventType = event.getEventType();
            eventTitle = event.getTitle();
            daysUntilEvent = (int) ChronoUnit.DAYS.between(LocalDate.now(), event.getEventDate());
            userId = event.getUser().getId();
        }

        // 컨텍스트 빌드
        String context = buildContext(eventType, request);

        // AI API 호출
        List<AIRecommendationDto.GiftRecommendation> recommendations;
        try {
            recommendations = callAIAPI(context, request);
        } catch (Exception e) {
            log.error("AI API call failed, providing fallback recommendations", e);
            // Fallback: 기본 추천 제공
            recommendations = getFallbackRecommendations(eventType, request);
        }

        // 사용자 저장 선물과 매칭 및 우선 정렬
        if (userId != null) {
            recommendations = matchAndPrioritizeUserGifts(recommendations, userId);
        }

        return AIRecommendationDto.RecommendResponse.builder()
                .recommendations(recommendations)
                .eventTitle(eventTitle)
                .eventType(eventType)
                .daysUntilEvent(daysUntilEvent)
                .build();
    }

    /**
     * 컨텍스트 빌더
     */
    private String buildContext(Event.EventType eventType, AIRecommendationDto.RecommendRequest request) {
        StringBuilder context = new StringBuilder();

        // 이벤트 타입별 설명
        context.append("이벤트 유형: ").append(getEventTypeDescription(eventType)).append("\n");

        // 관계
        if (request.getRelationship() != null && !request.getRelationship().isEmpty()) {
            context.append("관계: ").append(request.getRelationship()).append("\n");
        }

        // 예산
        if (request.getMinBudget() != null || request.getMaxBudget() != null) {
            context.append("예산: ");
            if (request.getMinBudget() != null && request.getMaxBudget() != null) {
                context.append(String.format("%,d원 ~ %,d원", request.getMinBudget(), request.getMaxBudget()));
            } else if (request.getMinBudget() != null) {
                context.append(String.format("%,d원 이상", request.getMinBudget()));
            } else {
                context.append(String.format("%,d원 이하", request.getMaxBudget()));
            }
            context.append("\n");
        }

        // 추가 정보
        if (request.getAdditionalInfo() != null && !request.getAdditionalInfo().isEmpty()) {
            context.append("추가 정보: ").append(request.getAdditionalInfo()).append("\n");
        }

        return context.toString();
    }

    /**
     * AI API 호출 (OpenAI GPT)
     */
    private List<AIRecommendationDto.GiftRecommendation> callAIAPI(String context, AIRecommendationDto.RecommendRequest request) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("AI API key is not configured, using fallback");
            throw new CustomException(ErrorCode.AI_SERVICE_UNAVAILABLE);
        }

        try {
            String prompt = buildPrompt(context);

            // OpenAI API 요청
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", Arrays.asList(
                    Map.of("role", "system", "content", "당신은 선물 추천 전문가입니다. 주어진 상황에 맞는 5개의 선물을 추천해주세요."),
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1500);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 응답 파싱
            return parseAIResponse(response.getBody());

        } catch (Exception e) {
            log.error("Failed to call AI API", e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * 프롬프트 생성
     */
    private String buildPrompt(String context) {
        return String.format("""
                다음 정보를 바탕으로 적합한 선물 5개를 추천해주세요:

                %s

                각 선물에 대해 다음 정보를 JSON 배열 형식으로 제공해주세요:
                [
                  {
                    "name": "선물 이름",
                    "description": "선물에 대한 간단한 설명 (50자 이내)",
                    "reason": "이 선물을 추천하는 이유 (100자 이내)",
                    "estimatedPrice": 예상가격(숫자만),
                    "category": "카테고리 (FLOWER, JEWELRY, COSMETICS, FASHION, ELECTRONICS, FOOD, EXPERIENCE, BOOK, HOBBY, OTHER 중 하나)"
                  }
                ]

                JSON 형식으로만 응답해주세요. 다른 설명은 포함하지 마세요.
                """, context);
    }

    /**
     * AI 응답 파싱
     */
    private List<AIRecommendationDto.GiftRecommendation> parseAIResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // JSON 배열 추출 (마크다운 코드 블록 제거)
            content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

            JsonNode recommendations = objectMapper.readTree(content);
            List<AIRecommendationDto.GiftRecommendation> result = new ArrayList<>();

            for (JsonNode node : recommendations) {
                GiftItem.GiftCategory category;
                try {
                    category = GiftItem.GiftCategory.valueOf(node.path("category").asText());
                } catch (Exception e) {
                    category = GiftItem.GiftCategory.OTHER;
                }

                result.add(AIRecommendationDto.GiftRecommendation.builder()
                        .name(node.path("name").asText())
                        .description(node.path("description").asText())
                        .reason(node.path("reason").asText())
                        .estimatedPrice(node.path("estimatedPrice").asInt())
                        .category(category)
                        .isUserSaved(false)
                        .savedGiftId(null)
                        .build());
            }

            return result;

        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response", e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * Fallback 추천 (AI API 실패 시)
     */
    private List<AIRecommendationDto.GiftRecommendation> getFallbackRecommendations(
            Event.EventType eventType, AIRecommendationDto.RecommendRequest request) {

        List<AIRecommendationDto.GiftRecommendation> fallbacks = new ArrayList<>();

        // 이벤트 타입별 기본 추천
        switch (eventType) {
            case BIRTHDAY:
                fallbacks.add(createRecommendation("생일 케이크", "특별한 날을 위한 맛있는 케이크", "생일의 필수 아이템", 50000, GiftItem.GiftCategory.FOOD));
                fallbacks.add(createRecommendation("향수", "기억에 남을 향기로운 선물", "개성을 표현하는 향수", 100000, GiftItem.GiftCategory.COSMETICS));
                fallbacks.add(createRecommendation("꽃다발", "마음을 전하는 아름다운 꽃", "특별한 날의 정성", 30000, GiftItem.GiftCategory.FLOWER));
                break;
            case ANNIVERSARY_100:
            case ANNIVERSARY_200:
            case ANNIVERSARY_300:
            case ANNIVERSARY_1YEAR:
                fallbacks.add(createRecommendation("커플링", "영원한 사랑의 증표", "기념일의 상징적인 선물", 200000, GiftItem.GiftCategory.JEWELRY));
                fallbacks.add(createRecommendation("커플 시계", "함께하는 시간을 새기는 선물", "실용적이고 의미있는 선물", 150000, GiftItem.GiftCategory.FASHION));
                fallbacks.add(createRecommendation("레스토랑 예약", "특별한 식사 경험", "추억을 만드는 시간", 100000, GiftItem.GiftCategory.EXPERIENCE));
                break;
            case VALENTINES_DAY:
                fallbacks.add(createRecommendation("초콜릿 세트", "사랑을 전하는 달콤한 선물", "발렌타인데이의 정석", 30000, GiftItem.GiftCategory.FOOD));
                fallbacks.add(createRecommendation("장미 꽃다발", "사랑의 상징 붉은 장미", "로맨틱한 분위기 연출", 50000, GiftItem.GiftCategory.FLOWER));
                break;
            case WHITE_DAY:
                fallbacks.add(createRecommendation("사탕 세트", "화이트데이 답례 선물", "달콤한 마음 전달", 20000, GiftItem.GiftCategory.FOOD));
                fallbacks.add(createRecommendation("액세서리", "패션 포인트가 되는 선물", "스타일을 높이는 아이템", 80000, GiftItem.GiftCategory.FASHION));
                break;
            default:
                fallbacks.add(createRecommendation("선물 세트", "다양한 아이템으로 구성", "만능 선물 옵션", 50000, GiftItem.GiftCategory.OTHER));
        }

        // 예산에 맞게 필터링
        if (request.getMaxBudget() != null) {
            fallbacks.removeIf(rec -> rec.getEstimatedPrice() > request.getMaxBudget());
        }
        if (request.getMinBudget() != null) {
            fallbacks.removeIf(rec -> rec.getEstimatedPrice() < request.getMinBudget());
        }

        // 최소 3개는 보장
        if (fallbacks.size() < 3) {
            fallbacks.add(createRecommendation("기프트 카드", "원하는 것을 고를 수 있는 선물", "실용적인 선택", 50000, GiftItem.GiftCategory.OTHER));
            fallbacks.add(createRecommendation("도서 상품권", "지식과 감성을 선물", "의미있는 선물", 30000, GiftItem.GiftCategory.BOOK));
        }

        return fallbacks.subList(0, Math.min(5, fallbacks.size()));
    }

    private AIRecommendationDto.GiftRecommendation createRecommendation(
            String name, String description, String reason, int price, GiftItem.GiftCategory category) {
        return AIRecommendationDto.GiftRecommendation.builder()
                .name(name)
                .description(description)
                .reason(reason)
                .estimatedPrice(price)
                .category(category)
                .isUserSaved(false)
                .savedGiftId(null)
                .build();
    }

    /**
     * 이벤트 타입 설명
     */
    private String getEventTypeDescription(Event.EventType eventType) {
        if (eventType == null) return "특별한 날";

        return switch (eventType) {
            case BIRTHDAY -> "생일";
            case ANNIVERSARY_100 -> "100일 기념일";
            case ANNIVERSARY_200 -> "200일 기념일";
            case ANNIVERSARY_300 -> "300일 기념일";
            case ANNIVERSARY_1YEAR -> "1주년 기념일";
            case VALENTINES_DAY -> "발렌타인데이";
            case WHITE_DAY -> "화이트데이";
            case PEPERO_DAY -> "빼빼로데이";
            case CHRISTMAS -> "크리스마스";
            case NEW_YEAR -> "새해";
            default -> "특별한 날";
        };
    }

    /**
     * 사용자 저장 선물과 AI 추천 매칭 및 우선 정렬
     */
    private List<AIRecommendationDto.GiftRecommendation> matchAndPrioritizeUserGifts(
            List<AIRecommendationDto.GiftRecommendation> recommendations, Long userId) {

        // 사용자의 모든 저장된 선물 조회
        List<GiftItem> userGifts = giftItemRepository.findByUserId(userId);

        // AI 추천과 사용자 저장 선물 매칭
        for (AIRecommendationDto.GiftRecommendation recommendation : recommendations) {
            for (GiftItem userGift : userGifts) {
                // 이름 유사도 또는 카테고리 매칭으로 판단
                if (isMatchingGift(recommendation, userGift)) {
                    recommendation = AIRecommendationDto.GiftRecommendation.builder()
                            .name(recommendation.getName())
                            .description(recommendation.getDescription())
                            .reason(recommendation.getReason())
                            .estimatedPrice(recommendation.getEstimatedPrice())
                            .category(recommendation.getCategory())
                            .purchaseLink(recommendation.getPurchaseLink())
                            .isUserSaved(true)
                            .savedGiftId(userGift.getId())
                            .build();
                    break;
                }
            }

            // isUserSaved가 null이면 false로 설정
            if (recommendation.getIsUserSaved() == null) {
                recommendations.set(recommendations.indexOf(recommendation),
                        AIRecommendationDto.GiftRecommendation.builder()
                                .name(recommendation.getName())
                                .description(recommendation.getDescription())
                                .reason(recommendation.getReason())
                                .estimatedPrice(recommendation.getEstimatedPrice())
                                .category(recommendation.getCategory())
                                .purchaseLink(recommendation.getPurchaseLink())
                                .isUserSaved(false)
                                .savedGiftId(null)
                                .build());
            }
        }

        // 사용자 저장 선물을 우선 정렬 (isUserSaved = true를 앞으로)
        recommendations.sort((r1, r2) -> {
            boolean r1Saved = r1.getIsUserSaved() != null && r1.getIsUserSaved();
            boolean r2Saved = r2.getIsUserSaved() != null && r2.getIsUserSaved();
            return Boolean.compare(r2Saved, r1Saved);
        });

        return recommendations;
    }

    /**
     * AI 추천과 사용자 저장 선물 매칭 여부 판단
     */
    private boolean isMatchingGift(AIRecommendationDto.GiftRecommendation recommendation, GiftItem userGift) {
        // 1. 이름 유사도 체크 (대소문자 무시, 공백 제거)
        String recName = recommendation.getName().toLowerCase().replaceAll("\\s+", "");
        String giftName = userGift.getName().toLowerCase().replaceAll("\\s+", "");

        if (recName.contains(giftName) || giftName.contains(recName)) {
            return true;
        }

        // 2. 카테고리 및 가격대가 유사한 경우
        if (recommendation.getCategory() == userGift.getCategory()) {
            if (userGift.getPrice() != null && recommendation.getEstimatedPrice() != null) {
                // 가격이 ±30% 범위 내에 있으면 유사한 것으로 판단
                int priceDiff = Math.abs(userGift.getPrice() - recommendation.getEstimatedPrice());
                int priceAvg = (userGift.getPrice() + recommendation.getEstimatedPrice()) / 2;
                if (priceDiff < priceAvg * 0.3) {
                    return true;
                }
            }
        }

        return false;
    }
}
