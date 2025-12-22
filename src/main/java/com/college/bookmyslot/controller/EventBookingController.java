package com.college.bookmyslot.controller;

import com.college.bookmyslot.dto.ApiResponse;
import com.college.bookmyslot.dto.EventBookingRequest;
import com.college.bookmyslot.dto.EventBookingResponse;
import com.college.bookmyslot.service.EventBookingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/bookings")
@CrossOrigin(origins = "*")
public class EventBookingController {

    private final EventBookingService eventBookingService;

    public EventBookingController(EventBookingService eventBookingService) {
        this.eventBookingService = eventBookingService;
    }

    @PostMapping("/book")
    public ApiResponse<EventBookingResponse> bookEvent(
            @RequestBody EventBookingRequest request
    ) {

        EventBookingResponse response = eventBookingService.bookEvent(request);

        return new ApiResponse<>(
                true,
                "Event booked successfully",
                response
        );
    }
}
