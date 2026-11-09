package com.example.hotel.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotel.entity.Customer;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

	// Find customer using email
    Optional<Customer> findByEmail(String email);

    // Check whether email already exists
    boolean existsByEmail(String email);

    // Find customer using phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Check whether phone number already exists
    boolean existsByPhoneNumber(String phoneNumber);
}