package com.quest.ordermanagement.order;

import static com.quest.ordermanagement.order.OrderResponseMapper.toOrderResponse;

import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.UpdateOrderStatusRequest;
import com.quest.ordermanagement.order.domain.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateOrderHandler {
    private final OrderRepository orderRepository;

    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        var order = orderRepository.getOrderById(orderId);
        order.updateStatus(request.getStatus());
        orderRepository.saveOrder(order);
        return toOrderResponse(order);
    }
}
