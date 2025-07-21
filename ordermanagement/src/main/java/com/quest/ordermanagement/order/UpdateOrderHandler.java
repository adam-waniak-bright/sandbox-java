package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.UpdateOrderStatusRequest;
import com.quest.ordermanagement.order.domain.repo.OrderEntityMapper;
import com.quest.ordermanagement.order.domain.repo.OrderRepository;
import com.quest.ordermanagement.order.error.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateOrderHandler {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        var order = orderRepository
                .findById(orderId)
                .map(orderEntityMapper::toDomain)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        order.setStatus(updateOrderStatusRequest.getStatus());
        orderRepository.save(orderEntityMapper.toEntity(order));
        return orderResponseMapper.toOrderResponse(order);
    }
}
