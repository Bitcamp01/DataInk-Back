package com.bit.datainkback.controller;

import com.bit.datainkback.dto.EventDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody EventDto eventDto) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        EventDto createdEvent = eventService.createEvent(loggedInUserId, eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        List<EventDto> events = eventService.getEventsByUserId(loggedInUserId);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<List<EventDto>> updateEvent(@PathVariable("id") Long id, @RequestBody EventDto eventDto,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            List<EventDto> updatedEvents = eventService.updateEvent(id, eventDto, customUserDetails.getUser().getUserId());
            return ResponseEntity.ok(updatedEvents);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<EventDto>> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<EventDto> remainingEvents = eventService.deleteEvent(id, customUserDetails.getUser().getUserId());
        return ResponseEntity.ok(remainingEvents);
    }
}

