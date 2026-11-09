package com.example.hotel.mapper;


import org.springframework.stereotype.Component;

import com.example.hotel.dto.response.CustomerResponse;
import com.example.hotel.entity.Customer;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {

        CustomerResponse response = new CustomerResponse();

        response.setId(customer.getId());
        response.setFullName(customer.getFullName());
        response.setEmail(customer.getEmail());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setRole(customer.getRole());
        response.setEnabled(customer.isEnabled());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());

        return response;
    }
}
