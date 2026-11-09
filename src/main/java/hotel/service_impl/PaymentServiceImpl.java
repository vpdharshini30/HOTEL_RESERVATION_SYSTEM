package com.example.hotel.service_impl;


import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotel.dto.response.PaymentResponse;
import com.example.hotel.entity.Payment;
import com.example.hotel.entity.Reservation;
import com.example.hotel.enums.PaymentStatus;
import com.example.hotel.enums.ReservationStatus;
import com.example.hotel.exception.DuplicateResourceException;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.mapper.PaymentMapper;
import com.example.hotel.repository.PaymentRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            ReservationRepository reservationRepository,
            PaymentMapper paymentMapper) {

        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.paymentMapper = paymentMapper;
    }

    // Process Payment
    @Override
    public Payment processPayment(
            Long reservationId,
            String paymentMethod) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with ID: "
                                        + reservationId));

        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Payment cannot be processed for this reservation");
        }

        if (paymentRepository.findByReservationId(reservationId).isPresent()) {
            throw new DuplicateResourceException(
                    "Payment already exists for this reservation");
        }

        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment method is required");
        }

        Payment payment = new Payment();

        payment.setTransactionId(generateTransactionId());
        payment.setAmount(reservation.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDescription(
                "Payment initiated for booking "
                        + reservation.getBookingNumber());
        payment.setReservation(reservation);

        return paymentRepository.save(payment);
    }

    // Get Payment By ID
    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with ID: " + id));
    }

    // Get Payment By Transaction ID
    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByTransactionId(String transactionId) {

        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found: " + transactionId));
    }

    // Get Payment By Reservation ID
    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByReservationId(Long reservationId) {

        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for reservation ID: "
                                        + reservationId));
    }

    // Get All Payments
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }

    // Get Payments By Status
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {

        return paymentRepository.findByStatus(status);
    }

    // Update Payment Status
    @Override
    public Payment updatePaymentStatus(
            Long id,
            PaymentStatus status) {

        Payment payment = getPaymentById(id);

        payment.setStatus(status);

        Reservation reservation = payment.getReservation();

        if (status == PaymentStatus.SUCCESS) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }

        if (status == PaymentStatus.FAILED) {
            reservation.setStatus(ReservationStatus.CANCELLED);
        }

        return paymentRepository.save(payment);
    }

    // Refund Payment
    @Override
    public Payment refundPayment(Long id) {

        Payment payment = getPaymentById(id);

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Only successful payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        Reservation reservation = payment.getReservation();
        reservation.setStatus(ReservationStatus.CANCELLED);

        return paymentRepository.save(payment);
    }

    // Get Payment Response By ID
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentResponseById(Long id) {

        Payment payment = getPaymentById(id);

        return paymentMapper.toResponse(payment);
    }

    // Get All Payment Responses
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPaymentResponses() {

        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    // Generate Transaction ID
    private String generateTransactionId() {

        return "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }
}