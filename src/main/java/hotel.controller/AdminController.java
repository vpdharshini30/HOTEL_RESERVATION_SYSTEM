package com.example.hotel.controller;



import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.dto.response.ApiResponse;
import com.example.hotel.dto.response.CustomerResponse;
import com.example.hotel.dto.response.PaymentResponse;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.service.Customerservice;
import com.example.hotel.service.PaymentService;
import com.example.hotel.service.ReservationService;
import com.example.hotel.service.RoomService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final Customerservice customerService;
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public AdminController(
            Customerservice customerService,
            RoomService roomService,
            ReservationService reservationService,
            PaymentService paymentService) {

        this.customerService = customerService;
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {

        List<CustomerResponse> customers =
                customerService.getAllCustomerResponses();

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Customers retrieved successfully",
                        customers));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {

        List<RoomResponse> rooms =
                roomService.getAllRoomResponses();

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Rooms retrieved successfully",
                        rooms));
    }

    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservations() {

        List<ReservationResponse> reservations =
                reservationService.getAllReservationResponses();

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Reservations retrieved successfully",
                        reservations));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {

        List<PaymentResponse> payments =
                paymentService.getAllPaymentResponses();

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Payments retrieved successfully",
                        payments));
    }
}
