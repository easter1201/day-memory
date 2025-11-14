package com.daymemory.service;

import com.daymemory.domain.dto.AIRecommendationDto;
import com.daymemory.domain.entity.AIRecommendation;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.domain.entity.RecommendedGiftItem;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.AIRecommendationRepository;
import com.daymemory.domain.repository.EventRepository;
import com.daymemory.domain.repository.GiftItemRepository;
import com.daymemory.domain.repository.RecommendedGiftItemRepository;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.security.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIRecommendationService {

    private final EventRepository eventRepository;
    private final GiftItemRepository giftItemRepository;
    private final AIRecommendationRepository aiRecommendationRepository;
    private final RecommendedGiftItemRepository recommendedGiftItemRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.provider:openai}")
    private String aiProvider;

    @Value("${ai.model:gpt-3.5-turbo}")
    private String model;

    @PostConstruct
    public void init() {
        log.info("=== AI Configuration ===");
        log.info("AI Provider: {}", aiProvider);
        log.info("AI Model: {}", model);
        log.info("API Key loaded: {}", apiKey != null && !apiKey.isEmpty() ? "Yes (masked: " + maskApiKey(apiKey) + ")" : "No");
        log.info("========================");
    }

    private String maskApiKey(String key) {
        if (key == null || key.length() < 8) return "****";
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }

    /**
     * AI 추천 이력 조회
     */
    public List<AIRecommendationDto.RecommendResponse> getRecommendations() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<AIRecommendation> recommendations = aiRecommendationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return recommendations.stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * AI 추천 상세 조회
     */
    public AIRecommendationDto.RecommendResponse getRecommendationById(Long id) {
        AIRecommendation recommendation = aiRecommendationRepository.findByIdWithUserAndEvent(id)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_RECOMMENDATION_NOT_FOUND));

        // 권한 체크: 본인의 추천 내역만 조회 가능
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!recommendation.getUser().getId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return convertToResponse(recommendation);
    }

    /**
     * AI 추천을 응답 DTO로 변환
     */
    private AIRecommendationDto.RecommendResponse convertToResponse(AIRecommendation recommendation) {
        // 추천된 선물 아이템 조회
        List<RecommendedGiftItem> recommendedItems = recommendedGiftItemRepository.findByRecommendationId(recommendation.getId());

        // 사용자의 저장된 선물 조회 (실시간 매칭을 위해)
        Long userId = recommendation.getUser().getId();
        List<GiftItem> userGifts = giftItemRepository.findByUserId(userId);

        // 이미 매칭된 GiftItem ID를 추적 (중복 매칭 방지)
        java.util.Set<Long> matchedGiftIds = new java.util.HashSet<>();

        // GiftRecommendation DTO 리스트 생성
        List<AIRecommendationDto.GiftRecommendation> giftRecommendations = recommendedItems.stream()
                .map(item -> {
                    // 실시간으로 사용자 선물과 매칭
                    GiftItem matchedGift = null;
                    if (item.getSavedGift() != null) {
                        // 이미 연결된 경우
                        matchedGift = item.getSavedGift();
                        matchedGiftIds.add(matchedGift.getId());
                    } else {
                        // 실시간 매칭 (이름, 카테고리, 가격대로 판단)
                        for (GiftItem userGift : userGifts) {
                            // 이미 다른 추천 아이템과 매칭된 선물은 건너뛰기
                            if (matchedGiftIds.contains(userGift.getId())) {
                                continue;
                            }

                            if (isMatchingGift(item, userGift)) {
                                matchedGift = userGift;
                                matchedGiftIds.add(userGift.getId());
                                // DB에 연결 저장
                                item.setSavedGift(userGift);
                                recommendedGiftItemRepository.save(item);
                                break;
                            }
                        }
                    }

                    return AIRecommendationDto.GiftRecommendation.builder()
                            .name(item.getName())
                            .description(item.getDescription())
                            .category(item.getCategory())
                            .estimatedPrice(item.getEstimatedPrice())
                            .reason(item.getReason())
                            .purchaseLink(item.getPurchaseLink())
                            .isUserSaved(matchedGift != null)
                            .savedGiftId(matchedGift != null ? matchedGift.getId() : null)
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());

        // 저장된 선물만 필터링
        List<AIRecommendationDto.GiftRecommendation> userSavedGifts = giftRecommendations.stream()
                .filter(gift -> gift.getIsUserSaved() != null && gift.getIsUserSaved())
                .collect(java.util.stream.Collectors.toList());

        // 응답 DTO 생성
        return AIRecommendationDto.RecommendResponse.builder()
                .id(recommendation.getId())
                .recommendations(giftRecommendations)
                .aiRecommendations(giftRecommendations)  // 동일한 목록
                .userSavedGifts(userSavedGifts)  // 저장된 것만
                .eventTitle(recommendation.getEventTitle())
                .eventType(recommendation.getEvent() != null ? recommendation.getEvent().getEventType() : null)
                .daysUntilEvent(recommendation.getDaysUntilEvent())
                .recipientName(recommendation.getRecipientName())
                .budget(recommendation.getBudget())
                .status(recommendation.getStatus() != null ? recommendation.getStatus().name() : "COMPLETED")
                .createdAt(recommendation.getCreatedAt() != null ? recommendation.getCreatedAt().toString() : null)
                .build();
    }

    /**
     * RecommendedGiftItem과 GiftItem 매칭 여부 판단
     * 매칭 조건을 엄격하게 적용하여 정확한 매칭만 허용
     */
    private boolean isMatchingGift(RecommendedGiftItem recommendedItem, GiftItem userGift) {
        // 이름 정규화 (대소문자 무시, 공백 제거)
        String recName = recommendedItem.getName().toLowerCase().replaceAll("\\s+", "");
        String giftName = userGift.getName().toLowerCase().replaceAll("\\s+", "");

        // 카테고리 체크
        boolean categoryMatch = recommendedItem.getCategory() == userGift.getCategory();

        // 가격 체크
        Integer recPrice = recommendedItem.getEstimatedPrice();
        Integer giftPrice = userGift.getEstimatedPrice() != null ? userGift.getEstimatedPrice() : userGift.getPrice();
        boolean priceMatch = false;

        if (recPrice != null && giftPrice != null) {
            // 가격이 ±20% 범위 내에 있는지 확인 (더 엄격하게)
            int priceDiff = Math.abs(giftPrice - recPrice);
            int priceAvg = (giftPrice + recPrice) / 2;
            priceMatch = priceDiff < priceAvg * 0.2;
        }

        // 이름이 거의 일치하면서 카테고리도 맞아야 매칭
        boolean exactNameMatch = recName.equals(giftName);

        // 이름의 길이가 5자 이상이고, 한쪽이 다른쪽을 완전히 포함하는 경우만 부분 매칭 허용
        boolean partialNameMatch = false;
        if (recName.length() >= 5 && giftName.length() >= 5) {
            partialNameMatch = recName.contains(giftName) || giftName.contains(recName);
        }

        // 매칭 조건: (정확한 이름 일치 OR (부분 이름 일치 AND 가격 일치)) AND 카테고리 일치
        return categoryMatch && (exactNameMatch || (partialNameMatch && priceMatch));
    }

    /**
     * AI 기반 선물 추천
     */
    public AIRecommendationDto.RecommendResponse recommendGifts(AIRecommendationDto.RecommendRequest request) {
        // 이벤트 정보 조회
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        Event.EventType eventType = event.getEventType();
        String eventTitle = event.getTitle();
        int daysUntilEvent = (int) ChronoUnit.DAYS.between(LocalDate.now(), event.getEventDate());
        Long userId = event.getUser().getId();

        // 컨텍스트 빌드 (이벤트 상세 정보 포함)
        String context = buildContext(event, daysUntilEvent, request);

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

        // DB에 추천 내역 저장
        AIRecommendation savedRecommendation = saveRecommendationToDatabase(request, event, recommendations, daysUntilEvent);

        return AIRecommendationDto.RecommendResponse.builder()
                .id(savedRecommendation.getId())
                .recommendations(recommendations)
                .eventTitle(eventTitle)
                .eventType(eventType)
                .daysUntilEvent(daysUntilEvent)
                .build();
    }

    /**
     * 추천 내역을 데이터베이스에 저장
     */
    private AIRecommendation saveRecommendationToDatabase(
            AIRecommendationDto.RecommendRequest request,
            Event event,
            List<AIRecommendationDto.GiftRecommendation> recommendations,
            int daysUntilEvent) {

        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 추천 내역 생성
        AIRecommendation recommendation = AIRecommendation.builder()
                .user(user)
                .event(event)
                .eventTitle(event.getTitle())
                .recipientName(event.getRecipientName())
                .recipientGender(request.getRecipientGender())
                .recipientAge(request.getRecipientAge())
                .budget(request.getBudget())
                .preferredCategories(request.getPreferredCategories() != null
                        ? String.join(",", request.getPreferredCategories())
                        : null)
                .additionalMessage(request.getAdditionalMessage())
                .daysUntilEvent(daysUntilEvent)
                .status(AIRecommendation.RecommendationStatus.COMPLETED)
                .build();

        AIRecommendation savedRecommendation = aiRecommendationRepository.save(recommendation);

        // 추천된 선물 아이템 저장
        for (AIRecommendationDto.GiftRecommendation giftRec : recommendations) {
            RecommendedGiftItem recommendedGift = RecommendedGiftItem.builder()
                    .recommendation(savedRecommendation)
                    .name(giftRec.getName())
                    .description(giftRec.getDescription())
                    .category(giftRec.getCategory())
                    .estimatedPrice(giftRec.getEstimatedPrice())
                    .reason(giftRec.getReason())
                    .purchaseLink(giftRec.getPurchaseLink())
                    .build();

            recommendedGiftItemRepository.save(recommendedGift);
        }

        log.info("Saved recommendation to database: id={}, event={}", savedRecommendation.getId(), event.getTitle());

        return savedRecommendation;
    }

    /**
     * 컨텍스트 빌더 (개선된 버전 - 이벤트 상세 정보 포함)
     */
    private String buildContext(Event event, int daysUntilEvent, AIRecommendationDto.RecommendRequest request) {
        StringBuilder context = new StringBuilder();

        // 이벤트 기본 정보
        context.append("=== 이벤트 정보 ===\n");
        context.append("이벤트 제목: ").append(event.getTitle()).append("\n");
        context.append("이벤트 유형: ").append(getEventTypeDescription(event.getEventType())).append("\n");
        context.append("이벤트 날짜: ").append(event.getEventDate()).append("\n");

        // D-day 정보
        if (daysUntilEvent > 0) {
            context.append("D-").append(daysUntilEvent).append(" (").append(daysUntilEvent).append("일 남음)\n");
        } else if (daysUntilEvent == 0) {
            context.append("오늘이 이벤트 날입니다!\n");
        } else {
            context.append("이벤트가 ").append(Math.abs(daysUntilEvent)).append("일 전에 지났습니다.\n");
        }

        // 받는 사람 정보
        context.append("\n=== 받는 사람 정보 ===\n");
        if (request.getRecipientGender() != null && !request.getRecipientGender().isEmpty()) {
            String genderText = request.getRecipientGender().equals("MALE") ? "남성" : "여성";
            context.append("성별: ").append(genderText).append("\n");
        }
        if (request.getRecipientAge() != null) {
            context.append("나이: ").append(request.getRecipientAge()).append("세\n");
        }

        // 예산 정보
        context.append("\n=== 예산 정보 ===\n");
        if (request.getBudget() != null) {
            context.append(String.format("예산: %,d원\n", request.getBudget()));
        } else {
            context.append("예산: 제한 없음\n");
        }

        // 선호 카테고리
        if (request.getPreferredCategories() != null && !request.getPreferredCategories().isEmpty()) {
            context.append("\n=== 선호 카테고리 ===\n");
            context.append(String.join(", ", request.getPreferredCategories())).append("\n");
            context.append("※ 위 카테고리의 선물을 우선적으로 추천해주세요\n");
        }

        // 제외할 선물 및 추가 메시지
        if (request.getAdditionalMessage() != null && !request.getAdditionalMessage().isEmpty()) {
            context.append("\n=== 중요: 제외할 선물 또는 특별 요청사항 ===\n");
            context.append(request.getAdditionalMessage()).append("\n");
            context.append("※ 위 내용을 반드시 고려하여 추천에서 제외하거나 특별히 고려해주세요\n");
        }

        return context.toString();
    }

    /**
     * AI API 호출 (Gemini 또는 OpenAI)
     */
    private List<AIRecommendationDto.GiftRecommendation> callAIAPI(String context, AIRecommendationDto.RecommendRequest request) {
        log.info("=== callAIAPI started ===");
        log.info("API Key present: {}", apiKey != null && !apiKey.isEmpty());
        log.info("API Provider: {}", aiProvider);
        log.info("API Model: {}", model);

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("AI API key is not configured, using fallback");
            throw new CustomException(ErrorCode.AI_SERVICE_UNAVAILABLE);
        }

        try {
            if ("gemini".equalsIgnoreCase(aiProvider)) {
                log.info("Using Gemini API for gift recommendations");
                return callGeminiAPI(context);
            } else {
                log.info("Using OpenAI API for gift recommendations");
                return callOpenAIAPI(context);
            }
        } catch (Exception e) {
            log.error("Failed to call AI API", e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * Gemini API 호출
     */
    private List<AIRecommendationDto.GiftRecommendation> callGeminiAPI(String context) {
        try {
            String prompt = buildPrompt(context);

            // Gemini API 요청 본문 구성
            Map<String, Object> requestBody = new HashMap<>();

            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(Map.of("text", prompt));

            List<Map<String, Object>> contents = new ArrayList<>();
            contents.add(Map.of("parts", parts));

            requestBody.put("contents", contents);

            // Gemini 설정
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 8192);  // 토큰 한도 증가
            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gemini API URL
            String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                    model, apiKey);

            log.info("Calling Gemini API with model: {}", model);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("Gemini API response status: {}", response.getStatusCode());
            log.info("Gemini API raw response: {}", response.getBody());

            // 응답 파싱
            return parseGeminiResponse(response.getBody());

        } catch (Exception e) {
            log.error("Failed to call Gemini API: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * OpenAI API 호출
     */
    private List<AIRecommendationDto.GiftRecommendation> callOpenAIAPI(String context) {
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
            return parseOpenAIResponse(response.getBody());

        } catch (Exception e) {
            log.error("Failed to call OpenAI API: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * 프롬프트 생성
     */
    private String buildPrompt(String context) {
        return String.format("""
                당신은 20년 경력의 선물 추천 전문가입니다.
                고객의 상황을 깊이 이해하고, 받는 사람이 진심으로 기뻐할 만한 선물을 추천하는 것이 당신의 전문 분야입니다.

                지금부터 아래 정보를 바탕으로 최고의 선물 5개를 추천해주세요:

                %s

                **추천 시 반드시 고려할 사항:**
                1. 받는 사람의 나이, 성별, 취향을 반드시 고려하세요
                2. 예산 범위 내에서 가성비가 좋은 선물을 우선적으로 추천하세요
                3. 이벤트의 특성과 의미를 생각하며, 감동을 줄 수 있는 선물을 선택하세요
                4. 실용성과 감성을 균형있게 고려하세요
                5. 선호 카테고리가 있다면 해당 카테고리의 선물을 우선적으로 추천하세요
                6. 제외할 선물이 있다면 절대 추천하지 마세요

                **중요 제약사항:**
                1. 제품명은 반드시 하나의 명확한 선물만 포함해야 합니다. "A 또는 B", "A나 B" 같은 선택지 형식은 절대 금지입니다.
                2. estimatedPrice는 예상 가격임을 명시하세요. 실제 구매 가격과 다를 수 있습니다.

                각 선물에 대해 다음 정보를 JSON 배열 형식으로 정확히 5개 제공해주세요:
                [
                  {
                    "name": "구체적인 선물 이름 하나만 (예: '조 말론 블랙베리 앤 베이 향수', '다이슨 에어랩', '애플 에어팟 프로')",
                    "description": "선물에 대한 매력적이고 상세한 설명 (2-3문장, 왜 특별한지 설명)",
                    "reason": "이 선물을 추천하는 구체적인 이유 (2-3문장, 받는 사람이 좋아할 포인트 강조)",
                    "estimatedPrice": 예상가격(숫자만, 원 단위),
                    "category": "카테고리 (FLOWER, JEWELRY, COSMETICS, FASHION, ELECTRONICS, FOOD, EXPERIENCE, BOOK, HOBBY, OTHER 중 정확히 하나)"
                  }
                ]

                중요: 반드시 JSON 배열 형식으로만 응답하세요. 마크다운(```json```)이나 추가 설명은 절대 포함하지 마세요.
                """, context);
    }

    /**
     * Gemini 응답 파싱
     */
    private List<AIRecommendationDto.GiftRecommendation> parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            // Gemini 응답 구조: candidates[0].content.parts[0].text
            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                log.error("No candidates in Gemini response");
                throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
            }

            String content = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            log.info("Gemini response content: {}", content.substring(0, Math.min(200, content.length())));

            // JSON 배열 추출 (마크다운 코드 블록 제거)
            content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

            return parseRecommendationsJson(content);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * OpenAI 응답 파싱
     */
    private List<AIRecommendationDto.GiftRecommendation> parseOpenAIResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            log.info("OpenAI response content: {}", content.substring(0, Math.min(200, content.length())));

            // JSON 배열 추출 (마크다운 코드 블록 제거)
            content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

            return parseRecommendationsJson(content);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse OpenAI response: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * 추천 JSON 파싱 (공통)
     */
    private List<AIRecommendationDto.GiftRecommendation> parseRecommendationsJson(String jsonContent) {
        try {
            JsonNode recommendations = objectMapper.readTree(jsonContent);
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
            log.error("Failed to parse recommendations JSON: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    /**
     * Fallback 추천 (AI API 실패 시)
     */
    private List<AIRecommendationDto.GiftRecommendation> getFallbackRecommendations(
            Event.EventType eventType, AIRecommendationDto.RecommendRequest request) {

        List<AIRecommendationDto.GiftRecommendation> allFallbacks = new ArrayList<>();

        // 선호 카테고리별 추천 생성
        if (request.getPreferredCategories() != null && !request.getPreferredCategories().isEmpty()) {
            for (String categoryStr : request.getPreferredCategories()) {
                try {
                    GiftItem.GiftCategory category = GiftItem.GiftCategory.valueOf(categoryStr);
                    allFallbacks.addAll(getRecommendationsByCategory(category, request.getBudget()));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid category: {}", categoryStr);
                }
            }
        }

        // 이벤트 타입별 기본 추천도 추가
        allFallbacks.addAll(getRecommendationsByEventType(eventType));

        // 예산에 맞게 필터링
        List<AIRecommendationDto.GiftRecommendation> filtered = new ArrayList<>();
        if (request.getBudget() != null) {
            int minBudget = (int) (request.getBudget() * 0.6);
            int maxBudget = (int) (request.getBudget() * 1.5);
            for (AIRecommendationDto.GiftRecommendation rec : allFallbacks) {
                if (rec.getEstimatedPrice() >= minBudget && rec.getEstimatedPrice() <= maxBudget) {
                    filtered.add(rec);
                }
            }
        } else {
            filtered.addAll(allFallbacks);
        }

        // 중복 제거 (이름 기준)
        Map<String, AIRecommendationDto.GiftRecommendation> uniqueMap = new LinkedHashMap<>();
        for (AIRecommendationDto.GiftRecommendation rec : filtered) {
            uniqueMap.putIfAbsent(rec.getName(), rec);
        }

        List<AIRecommendationDto.GiftRecommendation> result = new ArrayList<>(uniqueMap.values());

        // 최소 3개는 보장
        if (result.size() < 3) {
            if (request.getBudget() != null) {
                result.add(createRecommendation("기프트 카드", "원하는 것을 고를 수 있는 선물", "실용적인 선택",
                    request.getBudget(), GiftItem.GiftCategory.OTHER));
            } else {
                result.add(createRecommendation("기프트 카드", "원하는 것을 고를 수 있는 선물", "실용적인 선택",
                    50000, GiftItem.GiftCategory.OTHER));
            }
        }

        // 최대 5개 반환
        return result.subList(0, Math.min(5, result.size()));
    }

    /**
     * 카테고리별 추천 생성
     */
    private List<AIRecommendationDto.GiftRecommendation> getRecommendationsByCategory(
            GiftItem.GiftCategory category, Integer budget) {

        List<AIRecommendationDto.GiftRecommendation> recommendations = new ArrayList<>();
        int basePrice = budget != null ? budget : 50000;

        switch (category) {
            case FLOWER:
                recommendations.add(createRecommendation("프리미엄 꽃다발", "계절 꽃으로 구성한 아름다운 꽃다발", "마음을 전하는 정성",
                    (int)(basePrice * 0.8), category));
                recommendations.add(createRecommendation("장미 100송이", "특별한 날을 위한 화려한 장미", "로맨틱한 감동",
                    (int)(basePrice * 1.2), category));
                break;
            case JEWELRY:
                recommendations.add(createRecommendation("실버 목걸이", "세련된 디자인의 목걸이", "매일 착용 가능한 액세서리",
                    (int)(basePrice * 0.9), category));
                recommendations.add(createRecommendation("다이아몬드 귀걸이", "우아한 다이아몬드 포인트", "특별한 날의 선물",
                    (int)(basePrice * 1.3), category));
                break;
            case COSMETICS:
                recommendations.add(createRecommendation("프리미엄 스킨케어 세트", "유명 브랜드 스킨케어 세트", "피부 관리의 시작",
                    (int)(basePrice * 0.85), category));
                recommendations.add(createRecommendation("향수", "시그니처 향수", "개성을 표현하는 향기",
                    (int)(basePrice * 1.0), category));
                break;
            case FASHION:
                recommendations.add(createRecommendation("명품 지갑", "고급 가죽 지갑", "실용성과 품격을 모두",
                    (int)(basePrice * 1.1), category));
                recommendations.add(createRecommendation("디자이너 가방", "트렌디한 디자인", "스타일을 완성하는 아이템",
                    (int)(basePrice * 1.2), category));
                break;
            case ELECTRONICS:
                recommendations.add(createRecommendation("무선 이어폰", "프리미엄 사운드 경험", "일상의 필수품",
                    (int)(basePrice * 0.7), category));
                recommendations.add(createRecommendation("스마트 워치", "건강과 스타일을 한번에", "실용적인 스마트 기기",
                    (int)(basePrice * 1.0), category));
                break;
            case FOOD:
                recommendations.add(createRecommendation("고급 디저트 세트", "프리미엄 케이크와 디저트", "달콤한 행복",
                    (int)(basePrice * 0.6), category));
                recommendations.add(createRecommendation("레스토랑 식사권", "미슐랭 레스토랑 식사", "특별한 추억 만들기",
                    (int)(basePrice * 1.0), category));
                break;
            case EXPERIENCE:
                recommendations.add(createRecommendation("호텔 스테이", "럭셔리 호텔 패키지", "특별한 하루",
                    (int)(basePrice * 1.2), category));
                recommendations.add(createRecommendation("공연 티켓", "뮤지컬/콘서트 VIP석", "감동의 시간",
                    (int)(basePrice * 0.9), category));
                break;
            case BOOK:
                recommendations.add(createRecommendation("베스트셀러 도서 세트", "올해의 인기 도서", "지식과 감동",
                    (int)(basePrice * 0.5), category));
                recommendations.add(createRecommendation("도서 상품권", "원하는 책을 자유롭게", "실용적인 선택",
                    (int)(basePrice * 0.7), category));
                break;
            case HOBBY:
                recommendations.add(createRecommendation("취미 용품 세트", "전문가급 취미 도구", "열정을 응원",
                    (int)(basePrice * 0.9), category));
                recommendations.add(createRecommendation("클래스 수강권", "원하는 취미 배우기", "새로운 시작",
                    (int)(basePrice * 0.8), category));
                break;
            default:
                recommendations.add(createRecommendation("선물 세트", "다양한 구성", "만능 선물",
                    basePrice, category));
        }

        return recommendations;
    }

    /**
     * 이벤트 타입별 추천 생성
     */
    private List<AIRecommendationDto.GiftRecommendation> getRecommendationsByEventType(Event.EventType eventType) {
        List<AIRecommendationDto.GiftRecommendation> recommendations = new ArrayList<>();

        switch (eventType) {
            case BIRTHDAY:
                recommendations.add(createRecommendation("생일 케이크", "특별한 날을 위한 케이크", "생일의 필수", 50000, GiftItem.GiftCategory.FOOD));
                break;
            case ANNIVERSARY_100:
            case ANNIVERSARY_200:
            case ANNIVERSARY_300:
            case ANNIVERSARY_1YEAR:
                recommendations.add(createRecommendation("커플링", "영원한 사랑의 증표", "기념일 선물", 200000, GiftItem.GiftCategory.JEWELRY));
                break;
            case VALENTINES_DAY:
                recommendations.add(createRecommendation("초콜릿", "사랑을 전하는 달콤함", "발렌타인의 정석", 30000, GiftItem.GiftCategory.FOOD));
                break;
        }

        return recommendations;
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
