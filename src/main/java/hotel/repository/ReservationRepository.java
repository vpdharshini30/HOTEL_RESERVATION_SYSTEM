package com.example.hotel.repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hotel.entity.Reservation;
import com.example.hotel.enums.ReservationStatus;

@Repository
public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    // Find reservation using booking number
    Optional<Reservation> findByBookingNumber(String bookingNumber);

    // Check duplicate booking number
    boolean existsByBookingNumber(String bookingNumber);

    // Find all reservations of a customer
    List<Reservation> findByCustomerId(Long customerId);

    // Find reservations based on status
    List<Reservation> findByStatus(ReservationStatus status);

    // Find reservations of a particular room
    List<Reservation> findByRoomId(Long roomId);

    // Find customer booking history ordered by latest
    List<Reservation> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId
    );

    // Check overlapping reservations for a room
    @Query("""
        SELECT COUNT(r) > 0
        FROM Reservation r
        WHERE r.room.id = :roomId
        AND r.status IN :statuses
        AND r.checkInDate < :checkOutDate
        AND r.checkOutDate > :checkInDate
        """)
    boolean existsOverlappingReservation(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses") List<ReservationStatus> statuses
    );

    // Find reservations within date range
    List<Reservation> findByCheckInDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );
}