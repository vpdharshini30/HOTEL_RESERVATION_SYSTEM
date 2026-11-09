
package com.example.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.dto.request.PaymentRequest;
import com.example.hotel.dto.response.ApiResponse;
import com.example.hotel.dto.response.PaymentResponse;
import com.example.hotel.entity.Customer;
import com.example.hotel.entity.Payment;
import com.example.hotel.entity.Reservation;
import com.example.hotel.enums.PaymentStatus;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.CustomerRepository;
import com.example.hotel.service.PaymentService;
import com.example.hotel.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final CustomerRepository customerRepository;

    public PaymentController(
            PaymentService paymentService,
            ReservationService reservationService,
            CustomerRepository customerRepository) {

        this.paymentService = paymentService;
        this.reservationService = reservationService;
        this.customerRepository = customerRepository;
    }

    // Process Payment - Reservation Owner Only
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        Reservation reservation =
                reservationService.getReservationById(
                        request.getReservationId());

        checkReservationOwner(reservation, authentication);

        Payment payment = paymentService.processPayment(
                request.getReservationId(),
                request.getPaymentMethod());

        PaymentResponse response =
                paymentService.getPaymentResponseById(payment.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Payment initiated successfully",
                        response));
    }

    // Get Payment By ID - Owner or Admin
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long id,
            Authentication authentication) {

        Payment payment = paymentService.getPaymentById(id);

        checkReservationOwner(payment.getReservation(), authentication);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment fetched successfully",
                paymentService.getPaymentResponseById(id)));
    }

    // Get All Payments - Admin Only
    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payments fetched successfully",
                paymentService.getAllPaymentResponses()));
    }

    // Get Payment By Transaction ID - Owner or Admin
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByTransactionId(
            @PathVariable String transactionId,
            Authentication authentication) {

        Payment payment =
                paymentService.getPaymentByTransactionId(transactionId);

        checkReservationOwner(payment.getReservation(), authentication);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment fetched successfully",
                paymentService.getPaymentResponseById(payment.getId())));
    }

    // Get Payment By Reservation ID - Owner or Admin
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByReservationId(
            @PathVariable Long reservationId,
            Authentication authentication) {

        Reservation reservation =
                reservationService.getReservationById(reservationId);

        checkReservationOwner(reservation, authentication);

        Payment payment =
                paymentService.getPaymentByReservationId(reservationId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment fetched successfully",
                paymentService.getPaymentResponseById(payment.getId())));
    }

    // Get Payments By Status - Admin Only
    @GetMapping("/status/{status}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status) {

        List<PaymentResponse> payments =
                paymentService.getPaymentsByStatus(status)
                        .stream()
                        .map(payment -> paymentService
                                .getPaymentResponseById(payment.getId()))
                        .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payments fetched successfully",
                payments));
    }

    // Update Payment Status - Admin Only
    @PatchMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {

        Payment payment =
                paymentService.updatePaymentStatus(id, status);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment status updated successfully",
                paymentService.getPaymentResponseById(payment.getId())));
    }

    // Refund Payment - Admin Only
    @PatchMapping("/{id}/refund")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable Long id) {

        Payment payment = paymentService.refundPayment(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment refunded successfully",
                paymentService.getPaymentResponseById(payment.getId())));
    }

    // Check Reservation Owner
    private void checkReservationOwner(
            Reservation reservation,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        Customer customer = getLoggedInCustomer(authentication);

        if (!reservation.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to access this payment");
        }
    }

    // Get Logged In Customer
    private Customer getLoggedInCustomer(Authentication authentication) {

        return customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged in customer not found"));
    }
}
