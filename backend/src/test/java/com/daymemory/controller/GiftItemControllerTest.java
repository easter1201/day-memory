package com.daymemory.controller;

import com.daymemory.config.SecurityConfig;
import com.daymemory.domain.dto.GiftItemDto;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.security.CustomUserDetailsService;
import com.daymemory.security.JwtAuthenticationFilter;
import com.daymemory.security.JwtTokenProvider;
import com.daymemory.service.GiftItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = GiftItemController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@DisplayName("GiftItemController 테스트")
class GiftItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GiftItemService giftItemService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private GiftItemDto.Request createRequest;
    private GiftItemDto.Response giftResponse;

    @BeforeEach
    void setUp() {
        // Given: 선물 생성 요청 DTO
        createRequest = GiftItemDto.Request.builder()
                .eventId(1L)
                .nickname("Test Gift")
                .description("Test Description")
                .price(50000)
                .url("https://example.com/gift")
                .category(GiftItem.GiftCategory.ELECTRONICS)
                .build();

        // Given: 선물 응답 DTO
        giftResponse = GiftItemDto.Response.builder()
                .id(1L)
                .eventId(1L)
                .nickname("Test Gift")
                .description("Test Description")
                .price(50000)
                .url("https://example.com/gift")
                .category(GiftItem.GiftCategory.ELECTRONICS)
                .isPurchased(false)
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gifts - 선물 생성 성공")
    void testCreateGiftItem_Success() throws Exception {
        // Given
        given(giftItemService.createGiftItem(eq(1L), any(GiftItemDto.Request.class)))
                .willReturn(giftResponse);

        // When & Then
        mockMvc.perform(post("/api/gifts")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Gift"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(50000))
                .andExpect(jsonPath("$.category").value("ELECTRONICS"))
                .andExpect(jsonPath("$.isPurchased").value(false));

        // Verify
        then(giftItemService).should(times(1)).createGiftItem(eq(1L), any(GiftItemDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gifts - 유효성 검증 실패 (빈 이름)")
    void testCreateGiftItem_ValidationFailed_EmptyName() throws Exception {
        // Given
        GiftItemDto.Request invalidRequest = GiftItemDto.Request.builder()
                .nickname("")  // 빈 이름
                .price(50000)
                .build();

        // When & Then
        mockMvc.perform(post("/api/gifts")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(giftItemService).should(never()).createGiftItem(anyLong(), any(GiftItemDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gifts - 사용자를 찾을 수 없음")
    void testCreateGiftItem_UserNotFound() throws Exception {
        // Given
        given(giftItemService.createGiftItem(eq(999L), any(GiftItemDto.Request.class)))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/api/gifts")
                        .with(csrf())
                        .param("userId", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(giftItemService).should(times(1)).createGiftItem(eq(999L), any(GiftItemDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gifts - 선물 목록 조회 성공")
    void testGetGiftItems_Success() throws Exception {
        // Given
        List<GiftItemDto.Response> giftList = List.of(giftResponse);
        given(giftItemService.getGiftItemsByUser(1L)).willReturn(giftList);

        // When & Then
        mockMvc.perform(get("/api/gifts")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Gift"));

        // Verify
        then(giftItemService).should(times(1)).getGiftItemsByUser(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gifts - 미구매 선물 조회")
    void testGetGiftItems_Unpurchased() throws Exception {
        // Given
        List<GiftItemDto.Response> unpurchasedGifts = List.of(giftResponse);
        given(giftItemService.getUnpurchasedGiftItems(1L)).willReturn(unpurchasedGifts);

        // When & Then
        mockMvc.perform(get("/api/gifts")
                        .param("userId", "1")
                        .param("purchased", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isPurchased").value(false));

        // Verify
        then(giftItemService).should(times(1)).getUnpurchasedGiftItems(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gifts - 카테고리별 조회")
    void testGetGiftItems_ByCategory() throws Exception {
        // Given
        List<GiftItemDto.Response> giftList = List.of(giftResponse);
        given(giftItemService.getGiftItemsByCategory(1L, GiftItem.GiftCategory.ELECTRONICS))
                .willReturn(giftList);

        // When & Then
        mockMvc.perform(get("/api/gifts")
                        .param("userId", "1")
                        .param("category", "ELECTRONICS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("ELECTRONICS"));

        // Verify
        then(giftItemService).should(times(1)).getGiftItemsByCategory(1L, GiftItem.GiftCategory.ELECTRONICS);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gifts/event/{eventId} - 이벤트별 선물 조회")
    void testGetGiftItemsByEvent_Success() throws Exception {
        // Given
        List<GiftItemDto.Response> giftList = List.of(giftResponse);
        given(giftItemService.getGiftItemsByEvent(1L)).willReturn(giftList);

        // When & Then
        mockMvc.perform(get("/api/gifts/event/{eventId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].eventId").value(1));

        // Verify
        then(giftItemService).should(times(1)).getGiftItemsByEvent(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/gifts/{giftId} - 선물 수정 성공")
    void testUpdateGiftItem_Success() throws Exception {
        // Given
        GiftItemDto.Request updateRequest = GiftItemDto.Request.builder()
                .nickname("Updated Gift")
                .description("Updated Description")
                .price(60000)
                .url("https://example.com/updated")
                .category(GiftItem.GiftCategory.FASHION)
                .build();

        GiftItemDto.Response updatedResponse = GiftItemDto.Response.builder()
                .id(1L)
                .nickname("Updated Gift")
                .description("Updated Description")
                .price(60000)
                .url("https://example.com/updated")
                .category(GiftItem.GiftCategory.FASHION)
                .isPurchased(false)
                .build();

        given(giftItemService.updateGiftItem(eq(1L), any(GiftItemDto.Request.class)))
                .willReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/gifts/{giftId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Gift"))
                .andExpect(jsonPath("$.price").value(60000));

        // Verify
        then(giftItemService).should(times(1)).updateGiftItem(eq(1L), any(GiftItemDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/gifts/{giftId} - 선물을 찾을 수 없음")
    void testUpdateGiftItem_NotFound() throws Exception {
        // Given
        given(giftItemService.updateGiftItem(eq(999L), any(GiftItemDto.Request.class)))
                .willThrow(new CustomException(ErrorCode.GIFT_NOT_FOUND));

        // When & Then
        mockMvc.perform(put("/api/gifts/{giftId}", 999L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(giftItemService).should(times(1)).updateGiftItem(eq(999L), any(GiftItemDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/gifts/{giftId}/purchase - 구매 상태 토글 성공")
    void testTogglePurchaseStatus_Success() throws Exception {
        // Given
        GiftItemDto.Response purchasedResponse = GiftItemDto.Response.builder()
                .id(1L)
                .nickname("Test Gift")
                .isPurchased(true)
                .build();

        given(giftItemService.togglePurchaseStatus(1L)).willReturn(purchasedResponse);

        // When & Then
        mockMvc.perform(patch("/api/gifts/{giftId}/purchase", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isPurchased").value(true));

        // Verify
        then(giftItemService).should(times(1)).togglePurchaseStatus(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/gifts/{giftId}/purchase - 선물을 찾을 수 없음")
    void testTogglePurchaseStatus_NotFound() throws Exception {
        // Given
        given(giftItemService.togglePurchaseStatus(999L))
                .willThrow(new CustomException(ErrorCode.GIFT_NOT_FOUND));

        // When & Then
        mockMvc.perform(patch("/api/gifts/{giftId}/purchase", 999L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(giftItemService).should(times(1)).togglePurchaseStatus(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/gifts/{giftId} - 선물 삭제 성공")
    void testDeleteGiftItem_Success() throws Exception {
        // Given
        willDoNothing().given(giftItemService).deleteGiftItem(1L);

        // When & Then
        mockMvc.perform(delete("/api/gifts/{giftId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify
        then(giftItemService).should(times(1)).deleteGiftItem(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/gifts/{giftId} - 선물을 찾을 수 없음")
    void testDeleteGiftItem_NotFound() throws Exception {
        // Given
        willThrow(new CustomException(ErrorCode.GIFT_NOT_FOUND))
                .given(giftItemService).deleteGiftItem(999L);

        // When & Then
        mockMvc.perform(delete("/api/gifts/{giftId}", 999L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(giftItemService).should(times(1)).deleteGiftItem(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gifts/search - 선물 검색 성공")
    void testSearchGiftItems_Success() throws Exception {
        // Given
        List<GiftItemDto.Response> searchResults = List.of(giftResponse);
        given(giftItemService.searchGiftItems(1L, "Test")).willReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/gifts/search")
                        .param("userId", "1")
                        .param("keyword", "Test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Gift"));

        // Verify
        then(giftItemService).should(times(1)).searchGiftItems(1L, "Test");
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gifts/{giftId}/image - 선물 이미지 업로드 성공")
    void testUploadGiftImage_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        GiftItemDto.Response responseWithImage = GiftItemDto.Response.builder()
                .id(1L)
                .nickname("Test Gift")
                .imageUrl("https://example.com/images/test-image.jpg")
                .isPurchased(false)
                .build();

        given(giftItemService.uploadGiftImage(eq(1L), any())).willReturn(responseWithImage);

        // When & Then
        mockMvc.perform(multipart("/api/gifts/{giftId}/image", 1L)
                        .file(file)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/images/test-image.jpg"));

        // Verify
        then(giftItemService).should(times(1)).uploadGiftImage(eq(1L), any());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gifts/{giftId}/image - 선물을 찾을 수 없음")
    void testUploadGiftImage_NotFound() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        given(giftItemService.uploadGiftImage(eq(999L), any()))
                .willThrow(new CustomException(ErrorCode.GIFT_NOT_FOUND));

        // When & Then
        mockMvc.perform(multipart("/api/gifts/{giftId}/image", 999L)
                        .file(file)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(giftItemService).should(times(1)).uploadGiftImage(eq(999L), any());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/gifts/{giftId}/image - 선물 이미지 삭제 성공")
    void testDeleteGiftImage_Success() throws Exception {
        // Given
        willDoNothing().given(giftItemService).deleteGiftImage(1L);

        // When & Then
        mockMvc.perform(delete("/api/gifts/{giftId}/image", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify
        then(giftItemService).should(times(1)).deleteGiftImage(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/gifts/{giftId}/image - 선물을 찾을 수 없음")
    void testDeleteGiftImage_NotFound() throws Exception {
        // Given
        willThrow(new CustomException(ErrorCode.GIFT_NOT_FOUND))
                .given(giftItemService).deleteGiftImage(999L);

        // When & Then
        mockMvc.perform(delete("/api/gifts/{giftId}/image", 999L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(giftItemService).should(times(1)).deleteGiftImage(999L);
    }

    @Test
    @DisplayName("POST /api/gifts - 인증 없이 접근 시도 (401 Unauthorized)")
    void testCreateGiftItem_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/gifts")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        then(giftItemService).should(never()).createGiftItem(anyLong(), any(GiftItemDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gifts - 유효성 검증 실패 (음수 가격)")
    void testCreateGiftItem_ValidationFailed_NegativePrice() throws Exception {
        // Given
        GiftItemDto.Request invalidRequest = GiftItemDto.Request.builder()
                .nickname("Test Gift")
                .price(-1000)  // 음수 가격
                .build();

        // When & Then
        mockMvc.perform(post("/api/gifts")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(giftItemService).should(never()).createGiftItem(anyLong(), any(GiftItemDto.Request.class));
    }
}
