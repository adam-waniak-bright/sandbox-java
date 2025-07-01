package com.acti.sandboxjava.order.repository;

import com.acti.sandboxjava.order.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}

