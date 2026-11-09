package com.example.hotel.service;



import java.util.List;

import com.example.hotel.dto.response.CustomerResponse;
import com.example.hotel.entity.Customer;



public interface Customerservice {

    Customer registerCustomer(Customer customer);

    Customer getCustomerById(Long id);

    Customer getCustomerByEmail(String email);

    List<Customer> getAllCustomers();

    Customer updateCustomer(Long id, Customer customer);

    void deleteCustomer(Long id);

    // DTO Response Methods
    CustomerResponse getCustomerResponseById(Long id);

    List<CustomerResponse> getAllCustomerResponses();
}
