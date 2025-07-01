package com.acti.sandboxjava.order.service;

import com.acti.sandboxjava.order.domain.Customer;
import com.acti.sandboxjava.order.domain.CustomerStatus;
import com.acti.sandboxjava.order.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void validateCustomerIsActive(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new IllegalArgumentException("Customer is not active: " + customerId);
        }
    }
}

