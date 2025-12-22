package com.college.bookmyslot.dto;

import lombok.Data;

@Data
public class EventCheckInResponse {
    private boolean success;
    private String message;
    private String studentName;
    private String eventTitle;
    private String checkInTime;
    private String ticketId;
    private boolean checkedIn ;
}
