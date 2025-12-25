package com.college.bookmyslot.service;

import com.college.bookmyslot.dto.PaymentCreateRequest;
import com.college.bookmyslot.dto.PaymentResponse;
import com.college.bookmyslot.dto.PaymentVerifyRequest;
import com.college.bookmyslot.dto.EventBookingResponse;
import com.college.bookmyslot.model.Event;
import com.college.bookmyslot.model.Payment;
import com.college.bookmyslot.model.User;
import com.college.bookmyslot.repository.EventRepository;
import com.college.bookmyslot.repository.PaymentRepository;
import com.college.bookmyslot.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventBookingService eventBookingService;

    public PaymentService(PaymentRepository paymentRepository,
                          EventRepository eventRepository,
                          UserRepository userRepository,
                          EventBookingService eventBookingService) {
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventBookingService = eventBookingService;
    }

    public PaymentResponse createRazorpayOrder(PaymentCreateRequest request) {

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (event.getEventType() != Event.EventType.PAID) {
            throw new RuntimeException("This is not a paid event");
        }

        try {
            RazorpayClient client =
                    new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (event.getTicketPrice() * 100));
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "evt_" + event.getId());

            Order order = client.orders.create(orderRequest);

            Payment payment = new Payment();
            payment.setOrderId(order.get("id").toString());
            payment.setEvent(event);
            payment.setStudent(student);
            payment.setAmount(event.getTicketPrice());
            payment.setStatus(Payment.Status.CREATED);

            paymentRepository.save(payment);

            paymentRepository.save(payment);

            PaymentResponse response = new PaymentResponse();
            response.setOrderId(order.get("id").toString());
            response.setAmount(event.getTicketPrice());
            response.setStatus("CREATED");


            return response;

        } catch (Exception e) {
            throw new RuntimeException("Payment order creation failed");
        }
    }
    public EventBookingResponse verifyPayment(PaymentVerifyRequest request) {

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String payload = request.getOrderId() + "|" + request.getPaymentId();
        String expectedSignature =
                HmacUtils.hmacSha256Hex(razorpayKeySecret, payload);

        if (!expectedSignature.equals(request.getSignature())) {
            throw new RuntimeException("Invalid payment signature");
        }
        payment.setStatus(Payment.Status.SUCCESS);
        payment.setRazorpayPaymentId(request.getPaymentId());
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        return eventBookingService.bookEventAfterPayment(
                payment.getEvent(),
                payment.getStudent()
        );
    }
}
