package com.college.bookmyslot.controller;

import com.college.bookmyslot.dto.EventCreateRequest;
import com.college.bookmyslot.dto.EventListResponse;
import com.college.bookmyslot.dto.EventResponse;
import com.college.bookmyslot.dto.EventUpdateRequest;
import com.college.bookmyslot.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/club/{clubId}")
    public EventResponse createEvent(
            @PathVariable Long clubId,
            @RequestBody EventCreateRequest request
    ) {
        return eventService.createEvent(clubId, request);
    }

    @PutMapping("/{eventId}")
    public EventResponse updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventUpdateRequest request
    ) {
        return eventService.updateEvent(eventId, request);
    }

    @GetMapping
    public List<EventListResponse> getEventsByDate(
            @RequestParam String date
    ) {
        return eventService.listEventsByDate(date);
    }

    @GetMapping("/{eventId}")
    public EventResponse getEventDetails(
            @PathVariable Long eventId
    ) {
        return eventService.getEventDetails(eventId);
    }
}
