package com.college.bookmyslot.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String orderId;
    private double amount;
    private String status;
}

