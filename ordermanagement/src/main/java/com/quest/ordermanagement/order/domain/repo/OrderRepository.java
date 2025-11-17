package com.quest.ordermanagement.order.domain.repo;

import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.domain.Order;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface OrderRepository {
    void saveOrder(Order order);

    Optional<Order> findOrderById(String orderId);

    Order getOrderById(String orderId);

    Page<Order> findAllOrders(String customerId, OrderStatus status, int page, int limit);
}
