package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.model.OrderListResponse;
import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.domain.repo.InMemoryOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchOrderHandler {
    private final InMemoryOrderRepository orderRepository;
    private final OrderResponseMapper orderResponseMapper;

    public OrderResponse getOrder(String orderId) {
        return orderResponseMapper.toOrderResponse(orderRepository.getOrder(orderId));
    }

    public OrderListResponse listOrders(String customerId, OrderStatus status, Integer page, Integer limit) {
        return null;
    }
}
