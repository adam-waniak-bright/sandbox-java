package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.UpdateOrderStatusRequest;
import com.quest.ordermanagement.order.domain.repo.InMemoryOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateOrderHandler {
    private final InMemoryOrderRepository orderRepository;
    private final OrderResponseMapper orderResponseMapper;

    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        var order = orderRepository.getOrder(orderId);
        order.setStatus(updateOrderStatusRequest.getStatus());
        orderRepository.save(order);
        return orderResponseMapper.toOrderResponse(order);
    }
}
