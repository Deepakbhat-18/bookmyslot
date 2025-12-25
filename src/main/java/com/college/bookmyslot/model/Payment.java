package com.college.bookmyslot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String orderId;


    private String razorpayPaymentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private double amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        CREATED,
        SUCCESS,
        FAILED
    }
}


