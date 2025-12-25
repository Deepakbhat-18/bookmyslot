package com.college.bookmyslot.repository;

import com.college.bookmyslot.model.Event;
import com.college.bookmyslot.model.Payment;
import com.college.bookmyslot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByEvent(Event event);
    List<Payment> findByStudent(User student);
}


