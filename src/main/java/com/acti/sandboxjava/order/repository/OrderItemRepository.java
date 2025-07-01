package com.acti.sandboxjava.order.repository;

import com.acti.sandboxjava.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

}
