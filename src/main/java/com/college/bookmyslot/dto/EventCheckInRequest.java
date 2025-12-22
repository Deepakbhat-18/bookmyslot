package com.college.bookmyslot.dto;

import lombok.Data;

@Data
public class EventCheckInRequest {

    private String ticketId;
    private Long staffUserId;
}
