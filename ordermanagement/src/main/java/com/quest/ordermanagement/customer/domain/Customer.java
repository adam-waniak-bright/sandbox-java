package com.quest.ordermanagement.customer.domain;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
    private final String id;
    private String name;
    private String email;
    private CustomerStatus status;

    public Customer(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.status = CustomerStatus.ACTIVE;
    }

    public Customer(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = CustomerStatus.ACTIVE;
    }
}
