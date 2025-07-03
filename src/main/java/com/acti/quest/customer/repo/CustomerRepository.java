package com.acti.quest.customer.repo;

import com.acti.quest.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {}
