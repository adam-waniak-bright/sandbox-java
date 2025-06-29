package com.quest.ordermanagement.customer;

import static com.quest.ordermanagement.customer.domain.repo.InMemoryCustomerRepository.createCustomers;

import com.quest.ordermanagement.customer.domain.Customer;
import com.quest.ordermanagement.customer.error.CustomerNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class FetchCustomerHandler {
    public Customer getCustomer(String customerId) {
        return createCustomers().stream()
                .filter(customer -> customerId.equals(customer.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }

    public void verifyCustomerExists(String customerId) {
        createCustomers().stream()
                .filter(customer -> customerId.equals(customer.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }
}
