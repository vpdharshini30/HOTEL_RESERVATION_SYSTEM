package com.example.hotel.mapper;


import org.springframework.stereotype.Component;

import com.example.hotel.dto.response.PaymentResponse;
import com.example.hotel.entity.Payment;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setTransactionId(payment.getTransactionId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setPaymentDescription(
                payment.getPaymentDescription());
        response.setPaymentDate(payment.getPaymentDate());

        if (payment.getReservation() != null) {

            response.setReservationId(
                    payment.getReservation().getId());

            response.setBookingNumber(
                    payment.getReservation().getBookingNumber());
        }

        return response;
    }
}