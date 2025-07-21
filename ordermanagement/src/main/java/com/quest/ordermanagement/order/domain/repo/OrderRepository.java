package com.quest.ordermanagement.order.domain.repo;

import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.domain.Order;
import org.springframework.data.domain.Page;

public interface OrderRepository {
    void saveOrder(Order order);

    Order findOrderById(String orderId);

    Page<Order> findAllOrders(String customerId, OrderStatus status, int page, int limit);
}
