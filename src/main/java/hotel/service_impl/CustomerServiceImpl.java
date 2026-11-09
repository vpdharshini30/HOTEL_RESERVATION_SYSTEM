package com.example.hotel.service_impl;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotel.dto.response.CustomerResponse;
import com.example.hotel.entity.Customer;
import com.example.hotel.exception.DuplicateResourceException;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.mapper.CustomerMapper;
import com.example.hotel.repository.CustomerRepository;
import com.example.hotel.service.Customerservice;

@Service
@Transactional
public class CustomerServiceImpl implements Customerservice {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            CustomerMapper customerMapper) {

        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerMapper = customerMapper;
    }

    @Override
    public Customer registerCustomer(Customer customer) {

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered");
        }

        customer.setPassword(
                passwordEncoder.encode(customer.getPassword()));

        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByEmail(String email) {

        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomer(Long id, Customer updatedCustomer) {

        Customer existingCustomer = getCustomerById(id);

        if (!existingCustomer.getEmail().equals(updatedCustomer.getEmail())
                && customerRepository.existsByEmail(updatedCustomer.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already registered");
        }

        existingCustomer.setFullName(updatedCustomer.getFullName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());

        return customerRepository.save(existingCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {

        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerResponseById(Long id) {

        Customer customer = getCustomerById(id);

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomerResponses() {

        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }
}
