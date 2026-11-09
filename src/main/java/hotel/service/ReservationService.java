package com.example.hotel.service;


import java.time.LocalDate;
import java.util.List;

import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.entity.Reservation;
import com.example.hotel.enums.ReservationStatus;

public interface ReservationService {

    // Create a new reservation
    Reservation createReservation(
            Long customerId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfGuests,
            String specialRequest
    );

    // Get reservation by ID
    Reservation getReservationById(Long reservationId);

    // Get reservation by booking number
    Reservation getReservationByBookingNumber(String bookingNumber);

    // Get all reservations
    List<Reservation> getAllReservations();

    // Get reservations by customer
    List<Reservation> getReservationsByCustomer(Long customerId);

    // Get reservations by status
    List<Reservation> getReservationsByStatus(
            ReservationStatus status
    );

    // Cancel reservation
    Reservation cancelReservation(Long reservationId);

    // Update reservation status
    Reservation updateReservationStatus(
            Long reservationId,
            ReservationStatus status
    );

    // Get reservations between dates
    List<Reservation> getReservationsByDateRange(
            LocalDate startDate,
            LocalDate endDate
    );
    ReservationResponse getReservationResponseById(Long id);

    List<ReservationResponse> getAllReservationResponses();
    // Complete reservation
    Reservation completeReservation(Long reservationId);
}
