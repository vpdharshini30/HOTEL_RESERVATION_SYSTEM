package com.example.hotel.service_impl;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.entity.Customer;
import com.example.hotel.entity.Reservation;
import com.example.hotel.entity.Room;
import com.example.hotel.enums.ReservationStatus;
import com.example.hotel.enums.RoomStatus;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.exception.RoomNotAvailableException;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.repository.CustomerRepository;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.RoomTypeRepository;
import com.example.hotel.service.ReservationService;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            CustomerRepository customerRepository,
            RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository,
            ReservationMapper reservationMapper) {

        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.reservationMapper = reservationMapper;
    }

    // Create Reservation
    @Override
    public Reservation createReservation(
            Long customerId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfGuests,
            String specialRequest) {

        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required");
        }

        if (!checkInDate.isAfter(LocalDate.now().minusDays(1))) {
            throw new IllegalArgumentException(
                    "Check-in date must be today or a future date");
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date");
        }

        if (numberOfGuests == null || numberOfGuests <= 0) {
            throw new IllegalArgumentException(
                    "Number of guests must be greater than zero");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + customerId));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with ID: " + roomId));

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new RoomNotAvailableException(
                    "Room is not available");
        }

        if (room.getRoomType() == null
                || numberOfGuests > room.getRoomType().getCapacity()) {
            throw new IllegalArgumentException(
                    "Guest count exceeds room capacity");
        }

        boolean overlapping = reservationRepository.existsOverlappingReservation(
                roomId,
                checkInDate,
                checkOutDate,
                List.of(
                        ReservationStatus.PENDING,
                        ReservationStatus.CONFIRMED));

        if (overlapping) {
            throw new RoomNotAvailableException(
                    "Room is already reserved for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(
                checkInDate, checkOutDate);

        BigDecimal totalAmount = room.getRoomType()
                .getPricePerNight()
                .multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation();

        reservation.setBookingNumber(generateBookingNumber());
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfGuests(numberOfGuests);
        reservation.setTotalAmount(totalAmount);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setSpecialRequest(specialRequest);
        reservation.setCustomer(customer);
        reservation.setRoom(room);

        return reservationRepository.save(reservation);
    }

    // Get Reservation By ID
    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {

        return reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with ID: " + id));
    }

    // Get Reservation By Booking Number
    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationByBookingNumber(
            String bookingNumber) {

        return reservationRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found: " + bookingNumber));
    }

    // Get All Reservations
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {

        return reservationRepository.findAll();
    }

    // Get Reservations By Customer
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByCustomer(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found with ID: " + customerId);
        }

        return reservationRepository.findByCustomerId(customerId);
    }

    // Get Reservations By Status
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByStatus(
            ReservationStatus status) {

        return reservationRepository.findByStatus(status);
    }

    // Cancel Reservation
    @Override
    public Reservation cancelReservation(Long id) {

        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Reservation is already cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Completed reservation cannot be cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        return reservationRepository.save(reservation);
    }

    // Update Reservation Status
    @Override
    public Reservation updateReservationStatus(
            Long id,
            ReservationStatus status) {

        Reservation reservation = getReservationById(id);

        reservation.setStatus(status);

        return reservationRepository.save(reservation);
    }

    // Get Reservations By Date Range
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null
                || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "Invalid date range");
        }

        return reservationRepository.findByCheckInDateBetween(
                startDate, endDate);
    }

    // Complete Reservation
    @Override
    public Reservation completeReservation(Long id) {

        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only confirmed reservations can be completed");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);

        return reservationRepository.save(reservation);
    }

    // Get Reservation Response By ID
    @Transactional(readOnly = true)
    public ReservationResponse getReservationResponseById(Long id) {

        Reservation reservation = getReservationById(id);

        return reservationMapper.toResponse(reservation);
    }

    // Get All Reservation Responses
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservationResponses() {

        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    // Generate Booking Number
    private String generateBookingNumber() {

        return "BK-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }
}