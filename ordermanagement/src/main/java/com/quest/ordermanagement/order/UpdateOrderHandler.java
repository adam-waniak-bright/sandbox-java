package com.quest.ordermanagement.order;

import static com.quest.ordermanagement.order.OrderResponseMapper.toOrderResponse;

import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.api.model.UpdateOrderStatusRequest;
import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.domain.repo.OrderRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateOrderHandler {
    private final OrderRepository orderRepository;

    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        var order = orderRepository.findOrderById(orderId);
        order.setStatus(updateOrderStatusRequest.getStatus());
        updateOrderTimestamps(order, updateOrderStatusRequest.getStatus());
        orderRepository.saveOrder(order);
        return toOrderResponse(order);
    }

    private void updateOrderTimestamps(Order order, OrderStatus status) {
        switch (status) {
            case CONFIRMED -> order.setConfirmedAt(LocalDateTime.now());
            case SHIPPED -> order.setShippedAt(LocalDateTime.now());
            case DELIVERED -> order.setDeliveredAt(LocalDateTime.now());
            case CANCELLED -> order.setCancelledAt(LocalDateTime.now());
            default -> {
                // NOOP
            }
        }
    }
}
