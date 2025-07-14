package com.acti.quest.order.service;

import com.acti.order.model.OrderResponse;
import com.acti.order.model.OrderStatus;
import com.acti.order.model.UpdateOrderStatusRequest;
import com.acti.quest.order.domain.Order;
import com.acti.quest.order.error.InvalidStatusTransitionException;
import com.acti.quest.order.error.OrderNotFoundException;
import com.acti.quest.order.repo.OrderEntityMapper;
import com.acti.quest.order.repo.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpdateOrderHandler {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    @SneakyThrows
    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {

        log.info("Updating orderId: {} to status: {}", orderId, request.getStatus());

        Order order = orderRepository
                .findById(orderId)
                .map(orderEntityMapper::toDomain)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        isValidStatusToUpdate(request, order);

        order.setStatus(request.getStatus());
        orderRepository.save(orderEntityMapper.toEntity(order));

        return orderResponseMapper.toOrderResponse(order);
    }

    private void isValidStatusToUpdate(UpdateOrderStatusRequest request, Order order) {
        if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidStatusTransitionException(
                    "Cannot change status from " + order.getStatus() + " to " + request.getStatus());
        }
    }
}
