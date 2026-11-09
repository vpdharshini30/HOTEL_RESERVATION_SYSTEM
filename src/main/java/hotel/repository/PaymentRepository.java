package com.example.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotel.entity.Payment;
import com.example.hotel.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payment using transaction ID
    Optional<Payment> findByTransactionId(String transactionId);

    // Check whether transaction ID already exists
    boolean existsByTransactionId(String transactionId);

    // Find payment using reservation ID
    Optional<Payment> findByReservationId(Long reservationId);

    // Get payments based on payment status
    List<Payment> findByStatus(PaymentStatus status);

    // Get payments based on payment method
    List<Payment> findByPaymentMethod(String paymentMethod);
}
