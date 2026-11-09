package com.example.hotel.controller;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.dto.request.LoginRequest;
import com.example.hotel.dto.request.RegisterRequest;
import com.example.hotel.dto.response.ApiResponse;
import com.example.hotel.entity.Customer;
import com.example.hotel.security.JwtService;
import com.example.hotel.service.Customerservice;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final Customerservice customerService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            Customerservice customerService,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.customerService = customerService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Customer Registration
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        Customer customer = new Customer();

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPassword(request.getPassword());
        customer.setPhoneNumber(request.getPhoneNumber());

        customerService.registerCustomer(customer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Customer registered successfully",
                        "Registration completed"
                ));
    }

    // Customer Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String token = jwtService.generateToken(request.getEmail());

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Login successful",
                            Map.of("token", token)
                    )
            );

        } catch (AuthenticationException ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            false,
                            "Invalid email or password",
                            null
                    ));
        }
    }
}
