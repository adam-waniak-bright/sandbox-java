package com.acti.quest.customer.service;

import com.acti.quest.customer.domain.Customer;
import com.acti.quest.customer.domain.CustomerStatus;
import com.acti.quest.customer.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @SneakyThrows
    public void validateCustomerIsActive(String customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new IllegalArgumentException("Customer is not active: " + customerId);
        }
    }
}
