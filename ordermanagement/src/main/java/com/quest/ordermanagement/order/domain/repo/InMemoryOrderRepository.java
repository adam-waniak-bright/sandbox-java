package com.quest.ordermanagement.order.domain.repo;

import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.error.OrderNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InMemoryOrderRepository {
    private final OrderEntityMapper orderEntityMapper;
    Map<String, OrderEntity> orders = new HashMap<>();

    public Order save(Order order) {
        orders.put(order.getId(), orderEntityMapper.toEntity(order));
        return order;
    }

    public Optional<Order> findById(String orderId) {
        var orderEntity = orders.get(orderId);
        if (orderEntity == null) {
            return Optional.empty();
        }
        return Optional.of(orderEntityMapper.toDomain(orderEntity));
    }

    public Order getOrder(String orderId) {
        return Optional.ofNullable(orders.get(orderId))
                .map(orderEntityMapper::toDomain)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }
}
