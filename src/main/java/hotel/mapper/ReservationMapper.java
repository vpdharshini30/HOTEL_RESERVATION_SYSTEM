package com.example.hotel.mapper;


import org.springframework.stereotype.Component;

import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.entity.Reservation;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {

        ReservationResponse response = new ReservationResponse();

        response.setId(reservation.getId());
        response.setBookingNumber(reservation.getBookingNumber());
        response.setCheckInDate(reservation.getCheckInDate());
        response.setCheckOutDate(reservation.getCheckOutDate());
        response.setNumberOfGuests(reservation.getNumberOfGuests());
        response.setTotalAmount(reservation.getTotalAmount());
        response.setStatus(reservation.getStatus());
        response.setSpecialRequest(reservation.getSpecialRequest());
        response.setCreatedAt(reservation.getCreatedAt());

        if (reservation.getCustomer() != null) {
            response.setCustomerId(
                    reservation.getCustomer().getId());

            response.setCustomerName(
                    reservation.getCustomer().getFullName());
        }

        if (reservation.getRoom() != null) {
            response.setRoomId(
                    reservation.getRoom().getId());

            response.setRoomNumber(
                    reservation.getRoom().getRoomNumber());
        }

        return response;
    }
}