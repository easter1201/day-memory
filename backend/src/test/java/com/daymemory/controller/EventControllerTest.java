package com.daymemory.controller;

import com.daymemory.config.SecurityConfig;
import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.security.CustomUserDetailsService;
import com.daymemory.security.JwtAuthenticationFilter;
import com.daymemory.security.JwtTokenProvider;
import com.daymemory.service.EventService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = EventController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@DisplayName("EventController 테스트")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private EventDto.Request createRequest;
    private EventDto.Response eventResponse;

    @BeforeEach
    void setUp() {
        // Given: 이벤트 생성 요청 DTO
        createRequest = EventDto.Request.builder()
                .title("Test Event")
                .description("Test Description")
                .recipientName("John Doe")
                .relationship("Friend")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .isTracking(true)
                .reminderDays(List.of(1, 3, 7))
                .build();

        // Given: 이벤트 응답 DTO
        eventResponse = EventDto.Response.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Description")
                .recipientName("John Doe")
                .relationship("Friend")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isRecurring(false)
                .isActive(true)
                .isTracking(true)
                .dDay(30L)
                .reminders(new ArrayList<>())
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/events - 이벤트 생성 성공")
    void testCreateEvent_Success() throws Exception {
        // Given
        given(eventService.createEvent(eq(1L), any(EventDto.Request.class)))
                .willReturn(eventResponse);

        // When & Then
        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.eventType").value("BIRTHDAY"))
                .andExpect(jsonPath("$.isTracking").value(true));

        // Verify
        then(eventService).should(times(1)).createEvent(eq(1L), any(EventDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/events - 유효성 검증 실패 (빈 제목)")
    void testCreateEvent_ValidationFailed_EmptyTitle() throws Exception {
        // Given
        EventDto.Request invalidRequest = EventDto.Request.builder()
                .title("")  // 빈 제목
                .recipientName("John Doe")
                .relationship("Friend")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .build();

        // When & Then
        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(eventService).should(never()).createEvent(anyLong(), any(EventDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/events - 사용자를 찾을 수 없음")
    void testCreateEvent_UserNotFound() throws Exception {
        // Given
        given(eventService.createEvent(eq(999L), any(EventDto.Request.class)))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .param("userId", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(eventService).should(times(1)).createEvent(eq(999L), any(EventDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events - 이벤트 목록 조회 성공")
    void testGetEvents_Success() throws Exception {
        // Given
        List<EventDto.Response> eventList = List.of(eventResponse);
        given(eventService.getEventsByUser(1L)).willReturn(eventList);

        // When & Then
        mockMvc.perform(get("/api/events")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Event"));

        // Verify
        then(eventService).should(times(1)).getEventsByUser(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events - 타입별 이벤트 조회")
    void testGetEvents_ByType() throws Exception {
        // Given
        List<EventDto.Response> eventList = List.of(eventResponse);
        given(eventService.getEventsByUserAndType(1L, Event.EventType.BIRTHDAY))
                .willReturn(eventList);

        // When & Then
        mockMvc.perform(get("/api/events")
                        .param("userId", "1")
                        .param("type", "BIRTHDAY"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].eventType").value("BIRTHDAY"));

        // Verify
        then(eventService).should(times(1)).getEventsByUserAndType(1L, Event.EventType.BIRTHDAY);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/{eventId} - 이벤트 상세 조회 성공")
    void testGetEvent_Success() throws Exception {
        // Given
        given(eventService.getEvent(1L)).willReturn(eventResponse);

        // When & Then
        mockMvc.perform(get("/api/events/{eventId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        // Verify
        then(eventService).should(times(1)).getEvent(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/{eventId} - 이벤트를 찾을 수 없음")
    void testGetEvent_NotFound() throws Exception {
        // Given
        given(eventService.getEvent(999L))
                .willThrow(new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/api/events/{eventId}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(eventService).should(times(1)).getEvent(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/{eventId} - 이벤트 수정 성공")
    void testUpdateEvent_Success() throws Exception {
        // Given
        EventDto.Request updateRequest = EventDto.Request.builder()
                .title("Updated Event")
                .description("Updated Description")
                .recipientName("Jane Smith")
                .relationship("Sister")
                .eventDate(LocalDate.now().plusDays(40))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isRecurring(true)
                .build();

        EventDto.Response updatedResponse = EventDto.Response.builder()
                .id(1L)
                .title("Updated Event")
                .description("Updated Description")
                .recipientName("Jane Smith")
                .relationship("Sister")
                .eventDate(LocalDate.now().plusDays(40))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isRecurring(true)
                .isActive(true)
                .isTracking(true)
                .dDay(40L)
                .reminders(new ArrayList<>())
                .build();

        given(eventService.updateEvent(eq(1L), any(EventDto.Request.class)))
                .willReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/events/{eventId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Event"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        // Verify
        then(eventService).should(times(1)).updateEvent(eq(1L), any(EventDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/{eventId} - 이벤트 수정 실패 (존재하지 않는 이벤트)")
    void testUpdateEvent_NotFound() throws Exception {
        // Given
        given(eventService.updateEvent(eq(999L), any(EventDto.Request.class)))
                .willThrow(new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // When & Then
        mockMvc.perform(put("/api/events/{eventId}", 999L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(eventService).should(times(1)).updateEvent(eq(999L), any(EventDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/events/{eventId} - 이벤트 삭제 성공")
    void testDeleteEvent_Success() throws Exception {
        // Given
        willDoNothing().given(eventService).deleteEvent(1L);

        // When & Then
        mockMvc.perform(delete("/api/events/{eventId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify
        then(eventService).should(times(1)).deleteEvent(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/events/{eventId} - 이벤트 삭제 실패 (존재하지 않는 이벤트)")
    void testDeleteEvent_NotFound() throws Exception {
        // Given
        willThrow(new CustomException(ErrorCode.EVENT_NOT_FOUND))
                .given(eventService).deleteEvent(999L);

        // When & Then
        mockMvc.perform(delete("/api/events/{eventId}", 999L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(eventService).should(times(1)).deleteEvent(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/upcoming - 다가오는 이벤트 조회 성공")
    void testGetUpcomingEvents_Success() throws Exception {
        // Given
        List<EventDto.Response> upcomingEvents = List.of(eventResponse);
        given(eventService.getUpcomingEvents(1L, 30)).willReturn(upcomingEvents);

        // When & Then
        mockMvc.perform(get("/api/events/upcoming")
                        .param("userId", "1")
                        .param("days", "30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        // Verify
        then(eventService).should(times(1)).getUpcomingEvents(1L, 30);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/upcoming - 기본값(30일) 사용")
    void testGetUpcomingEvents_DefaultDays() throws Exception {
        // Given
        List<EventDto.Response> upcomingEvents = List.of(eventResponse);
        given(eventService.getUpcomingEvents(1L, 30)).willReturn(upcomingEvents);

        // When & Then
        mockMvc.perform(get("/api/events/upcoming")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Verify
        then(eventService).should(times(1)).getUpcomingEvents(1L, 30);
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/{eventId}/tracking - 추적 상태 토글 성공")
    void testToggleTracking_Success() throws Exception {
        // Given
        EventDto.ToggleTrackingRequest toggleRequest = EventDto.ToggleTrackingRequest.builder()
                .isTracking(false)
                .build();

        EventDto.Response toggledResponse = EventDto.Response.builder()
                .id(1L)
                .title("Test Event")
                .isTracking(false)
                .build();

        given(eventService.toggleTracking(1L, false)).willReturn(toggledResponse);

        // When & Then
        mockMvc.perform(put("/api/events/{eventId}/tracking", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toggleRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isTracking").value(false));

        // Verify
        then(eventService).should(times(1)).toggleTracking(1L, false);
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/{eventId}/reminders - 리마인더 설정 업데이트 성공")
    void testUpdateReminders_Success() throws Exception {
        // Given
        EventDto.UpdateReminderRequest reminderRequest = EventDto.UpdateReminderRequest.builder()
                .reminderDays(List.of(1, 3, 7, 14))
                .build();

        given(eventService.updateReminders(eq(1L), any(EventDto.UpdateReminderRequest.class)))
                .willReturn(eventResponse);

        // When & Then
        mockMvc.perform(put("/api/events/{eventId}/reminders", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reminderRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        // Verify
        then(eventService).should(times(1)).updateReminders(eq(1L), any(EventDto.UpdateReminderRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/{eventId}/reminders - 존재하지 않는 이벤트")
    void testUpdateReminders_NotFound() throws Exception {
        // Given
        EventDto.UpdateReminderRequest reminderRequest = EventDto.UpdateReminderRequest.builder()
                .reminderDays(List.of(1, 3, 7))
                .build();

        given(eventService.updateReminders(eq(999L), any(EventDto.UpdateReminderRequest.class)))
                .willThrow(new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // When & Then
        mockMvc.perform(put("/api/events/{eventId}/reminders", 999L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reminderRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify
        then(eventService).should(times(1)).updateReminders(eq(999L), any(EventDto.UpdateReminderRequest.class));
    }

    @Test
    @DisplayName("POST /api/events - 인증 없이 접근 시도 (401 Unauthorized)")
    void testCreateEvent_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        then(eventService).should(never()).createEvent(anyLong(), any(EventDto.Request.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/events - 유효성 검증 실패 (과거 날짜)")
    void testCreateEvent_ValidationFailed_PastDate() throws Exception {
        // Given
        EventDto.Request invalidRequest = EventDto.Request.builder()
                .title("Test Event")
                .recipientName("John Doe")
                .relationship("Friend")
                .eventDate(LocalDate.now().minusDays(1))  // 과거 날짜
                .eventType(Event.EventType.BIRTHDAY)
                .build();

        // When & Then
        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify
        then(eventService).should(never()).createEvent(anyLong(), any(EventDto.Request.class));
    }
}
