
package com.example.hotel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ApiResponse;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.entity.Customer;
import com.example.hotel.entity.Reservation;
import com.example.hotel.enums.ReservationStatus;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.CustomerRepository;
import com.example.hotel.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final CustomerRepository customerRepository;

    public ReservationController(
            ReservationService reservationService,
            CustomerRepository customerRepository) {

        this.reservationService = reservationService;
        this.customerRepository = customerRepository;
    }

    // Create Reservation
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        Customer customer = getLoggedInCustomer(authentication);

        Reservation reservation = reservationService.createReservation(
                customer.getId(),
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getNumberOfGuests(),
                request.getSpecialRequest());

        ReservationResponse response =
                reservationService.getReservationResponseById(
                        reservation.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Reservation created successfully",
                        response));
    }

    // Get Reservation By ID - Owner or Admin
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(
            @PathVariable Long id,
            Authentication authentication) {

        Reservation reservation =
                reservationService.getReservationById(id);

        checkReservationAccess(reservation, authentication);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservation fetched successfully",
                reservationService.getReservationResponseById(id)));
    }

    // Get All Reservations - Admin Only
    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservations() {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservations fetched successfully",
                reservationService.getAllReservationResponses()));
    }

    // Get Reservation By Booking Number - Owner or Admin
    @GetMapping("/booking/{bookingNumber}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationByBookingNumber(
            @PathVariable String bookingNumber,
            Authentication authentication) {

        Reservation reservation =
                reservationService.getReservationByBookingNumber(bookingNumber);

        checkReservationAccess(reservation, authentication);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservation fetched successfully",
                reservationService.getReservationResponseById(
                        reservation.getId())));
    }

    // Get My Reservations
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations(
            Authentication authentication) {

        Customer customer = getLoggedInCustomer(authentication);

        List<ReservationResponse> reservations =
                reservationService.getReservationsByCustomer(customer.getId())
                        .stream()
                        .map(reservation -> reservationService
                                .getReservationResponseById(reservation.getId()))
                        .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Your reservations fetched successfully",
                reservations));
    }

    // Get Reservations By Status - Admin Only
    @GetMapping("/status/{status}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationsByStatus(
            @PathVariable ReservationStatus status) {

        List<ReservationResponse> reservations =
                reservationService.getReservationsByStatus(status)
                        .stream()
                        .map(reservation -> reservationService
                                .getReservationResponseById(reservation.getId()))
                        .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservations fetched successfully",
                reservations));
    }

    // Cancel Reservation - Owner or Admin
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {

        Reservation existingReservation =
                reservationService.getReservationById(id);

        checkReservationAccess(existingReservation, authentication);

        Reservation reservation =
                reservationService.cancelReservation(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservation cancelled successfully",
                reservationService.getReservationResponseById(
                        reservation.getId())));
    }

    // Update Reservation Status - Admin Only
    @PatchMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {

        Reservation reservation =
                reservationService.updateReservationStatus(id, status);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservation status updated successfully",
                reservationService.getReservationResponseById(
                        reservation.getId())));
    }

    // Get Reservations By Date Range - Admin Only
    @GetMapping("/date-range")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<ReservationResponse> reservations =
                reservationService.getReservationsByDateRange(
                        startDate, endDate)
                        .stream()
                        .map(reservation -> reservationService
                                .getReservationResponseById(reservation.getId()))
                        .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservations fetched successfully",
                reservations));
    }

    // Complete Reservation - Admin Only
    @PatchMapping("/{id}/complete")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> completeReservation(
            @PathVariable Long id) {

        Reservation reservation =
                reservationService.completeReservation(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reservation completed successfully",
                reservationService.getReservationResponseById(
                        reservation.getId())));
    }

    // Check Reservation Owner
    private void checkReservationAccess(
            Reservation reservation,
            Authentication authentication) {

        if (authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"))) {
            return;
        }

        Customer customer = getLoggedInCustomer(authentication);

        if (!reservation.getCustomer().getId().equals(customer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to access this reservation");
        }
    }

    // Get Logged In Customer
    private Customer getLoggedInCustomer(Authentication authentication) {

        return customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged in customer not found"));
    }
}
