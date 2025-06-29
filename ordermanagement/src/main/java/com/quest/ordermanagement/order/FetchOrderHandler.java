package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.model.OrderListResponse;
import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.domain.repo.OrderEntityMapper;
import com.quest.ordermanagement.order.domain.repo.OrderRepository;
import com.quest.ordermanagement.order.error.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchOrderHandler {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    public OrderResponse getOrder(String orderId) {
        return orderRepository
                .findById(orderId)
                .map(orderEntityMapper::toDomain)
                .map(orderResponseMapper::toOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    public OrderListResponse listOrders(String customerId, OrderStatus status, Integer page, Integer limit) {
        return null;
    }
}
