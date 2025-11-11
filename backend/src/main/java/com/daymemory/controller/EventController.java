package com.daymemory.controller;

import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.service.EventService;
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

import java.util.List;

@Tag(name = "Event", description = "이벤트 관리 API - 생일, 기념일 등의 중요한 날짜를 관리합니다.")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "이벤트 생성", description = "새로운 이벤트(생일, 기념일 등)를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이벤트 생성 성공",
                    content = @Content(schema = @Schema(implementation = EventDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EventDto.Response> createEvent(
            @RequestParam Long userId,
            @RequestBody EventDto.Request request) {
        EventDto.Response response = eventService.createEvent(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트 목록 조회", description = "사용자의 이벤트 목록을 조회합니다. 타입을 지정하면 해당 타입의 이벤트만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이벤트 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventDto.Response.class)))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<EventDto.Response>> getEvents(
            @RequestParam Long userId,
            @RequestParam(required = false) Event.EventType type) {
        List<EventDto.Response> events;
        if (type != null) {
            events = eventService.getEventsByUserAndType(userId, type);
        } else {
            events = eventService.getEventsByUser(userId);
        }
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "이벤트 상세 조회", description = "특정 이벤트의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이벤트 조회 성공",
                    content = @Content(schema = @Schema(implementation = EventDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> getEvent(@PathVariable Long eventId) {
        EventDto.Response event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "이벤트 수정", description = "기존 이벤트의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이벤트 수정 성공",
                    content = @Content(schema = @Schema(implementation = EventDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventDto.Request request) {
        EventDto.Response response = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트 삭제", description = "이벤트를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "이벤트 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "다가오는 이벤트 조회", description = "지정된 기간 내에 다가오는 이벤트 목록을 조회합니다. (기본 30일)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다가오는 이벤트 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventDto.Response.class)))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDto.Response>> getUpcomingEvents(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "30") int days) {
        List<EventDto.Response> events = eventService.getUpcomingEvents(userId, days);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "이벤트 추적 토글", description = "이벤트의 추적 상태를 변경합니다. 추적 중인 이벤트만 리마인더가 발송됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추적 상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = EventDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PutMapping("/{eventId}/tracking")
    public ResponseEntity<EventDto.Response> toggleTracking(
            @PathVariable Long eventId,
            @RequestBody EventDto.ToggleTrackingRequest request) {
        EventDto.Response response = eventService.toggleTracking(eventId, request.getIsTracking());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리마인더 설정 업데이트", description = "이벤트의 리마인더 발송 일정을 업데이트합니다. (예: 1일 전, 7일 전 등)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리마인더 설정 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = EventDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.daymemory.exception.GlobalExceptionHandler.ErrorResponse.class)))
    })
    @PutMapping("/{eventId}/reminders")
    public ResponseEntity<EventDto.Response> updateReminders(
            @PathVariable Long eventId,
            @RequestBody EventDto.UpdateReminderRequest request) {
        EventDto.Response response = eventService.updateReminders(eventId, request);
        return ResponseEntity.ok(response);
    }
}
