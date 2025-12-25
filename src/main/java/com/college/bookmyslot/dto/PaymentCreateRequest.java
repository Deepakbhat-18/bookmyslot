package com.college.bookmyslot.dto;

import lombok.Data;

@Data
public class PaymentCreateRequest {
    private Long eventId;
    private Long studentId;
}

