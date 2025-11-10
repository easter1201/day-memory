package com.daymemory.controller;

import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto.Response> createEvent(
            @RequestParam Long userId,
            @RequestBody EventDto.Request request) {
        EventDto.Response response = eventService.createEvent(userId, request);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> getEvent(@PathVariable Long eventId) {
        EventDto.Response event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventDto.Request request) {
        EventDto.Response response = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDto.Response>> getUpcomingEvents(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "30") int days) {
        List<EventDto.Response> events = eventService.getUpcomingEvents(userId, days);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{eventId}/tracking")
    public ResponseEntity<EventDto.Response> toggleTracking(
            @PathVariable Long eventId,
            @RequestBody EventDto.ToggleTrackingRequest request) {
        EventDto.Response response = eventService.toggleTracking(eventId, request.getIsTracking());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{eventId}/reminders")
    public ResponseEntity<EventDto.Response> updateReminders(
            @PathVariable Long eventId,
            @RequestBody EventDto.UpdateReminderRequest request) {
        EventDto.Response response = eventService.updateReminders(eventId, request);
        return ResponseEntity.ok(response);
    }
}
