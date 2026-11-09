package com.example.hotel.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.dto.response.ApiResponse;
import com.example.hotel.dto.response.CustomerResponse;
import com.example.hotel.entity.Customer;
import com.example.hotel.service.Customerservice;


@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final Customerservice customerService;

    public CustomerController(Customerservice customerService) {
        this.customerService = customerService;
    }

    // Get logged-in customer profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(
            Authentication authentication) {

        Customer customer = customerService.getCustomerByEmail(
                authentication.getName());

        CustomerResponse response =
                customerService.getCustomerResponseById(customer.getId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile retrieved successfully",
                        response));
    }

    // Update logged-in customer profile
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateMyProfile(
            Authentication authentication,
            @RequestBody Customer updatedCustomer) {

        Customer customer = customerService.getCustomerByEmail(
                authentication.getName());

        Customer updated = customerService.updateCustomer(
                customer.getId(), updatedCustomer);

        CustomerResponse response =
                customerService.getCustomerResponseById(updated.getId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile updated successfully",
                        response));
    }

    // Delete logged-in customer account
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<String>> deleteMyAccount(
            Authentication authentication) {

        Customer customer = customerService.getCustomerByEmail(
                authentication.getName());

        customerService.deleteCustomer(customer.getId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Account deleted successfully",
                        "Customer account deleted"));
    }
}
