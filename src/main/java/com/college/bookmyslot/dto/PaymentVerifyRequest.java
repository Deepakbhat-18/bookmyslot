package com.college.bookmyslot.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequest {
    private String paymentId;
    private String orderId;
    private String signature;
}
