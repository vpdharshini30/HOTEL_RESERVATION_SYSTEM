package com.example.hotel.service;



import java.util.List;

import com.example.hotel.dto.response.PaymentResponse;
import com.example.hotel.entity.Payment;
import com.example.hotel.enums.PaymentStatus;

public interface PaymentService {

    // Process payment for a reservation
    Payment processPayment(
            Long reservationId,
            String paymentMethod
    );

    // Get payment by ID
    Payment getPaymentById(Long paymentId);

    // Get payment by transaction ID
    Payment getPaymentByTransactionId(String transactionId);

    // Get payment by reservation ID
    Payment getPaymentByReservationId(Long reservationId);

    // Get all payments
    List<Payment> getAllPayments();

    // Get payments by status
    List<Payment> getPaymentsByStatus(PaymentStatus status);

    // Update payment status
    Payment updatePaymentStatus(
            Long paymentId,
            PaymentStatus status
    );

    // Refund payment
    Payment refundPayment(Long paymentId);
    
    PaymentResponse getPaymentResponseById(Long id);

    List<PaymentResponse> getAllPaymentResponses();
}
