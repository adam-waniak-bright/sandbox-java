package com.quest.ordermanagement.customer.domain.repo;

import com.quest.ordermanagement.customer.domain.Customer;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InMemoryCustomerRepository {
    public static Customer createCustomer() {
        return new Customer("John Cena", "johncena@16.com");
    }

    public static List<Customer> createCustomers() {
        return List.of(
                new Customer("CUST1234", "John Cena", "johncena@16.com"),
                new Customer("CUST5678", "The Rock", "therock@08.com"),
                new Customer("CUST9012", "Stone Cold", "stonecold@316.com"));
    }
}
