package com.college.bookmyslot.dto;

import lombok.Data;

@Data
public class EventAnalyticsDto {

    private Long eventId;
    private String eventTitle;
    private String clubName;

    private int totalSeats;
    private int bookedSeats;
    private long checkedInCount;

    private double attendancePercentage;
    private double revenue;

    private String status;
}
