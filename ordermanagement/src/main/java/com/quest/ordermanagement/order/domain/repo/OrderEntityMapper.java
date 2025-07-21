package com.quest.ordermanagement.order.domain.repo;

import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.domain.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {
    public OrderEntity toEntity(Order order) {
        return new OrderEntity(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                toEntityOrderItem(order),
                order.getSubtotalAmount(),
                order.getTaxAmount(),
                order.getShippingAmount(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getConfirmedAt(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt());
    }

    private List<OrderItemEntity> toEntityOrderItem(Order order) {
        var items = order.getItems();
        return items.stream()
                .map(item -> new OrderItemEntity(
                        item.getId(),
                        createOrderEntity(order),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPriceCents(),
                        item.getLineTotalCents()))
                .toList();
    }

    private OrderEntity createOrderEntity(Order order) {
        return OrderEntity.builder().id(order.getId()).build();
    }

    public Order toDomain(OrderEntity orderEntity) {
        return new Order(
                orderEntity.getId(),
                orderEntity.getCustomerId(),
                orderEntity.getStatus(),
                toDomainOrderItem(orderEntity),
                orderEntity.getSubtotalAmount(),
                orderEntity.getTaxAmount(),
                orderEntity.getShippingAmount(),
                orderEntity.getTotalAmount(),
                orderEntity.getCreatedAt(),
                orderEntity.getConfirmedAt(),
                orderEntity.getShippedAt(),
                orderEntity.getDeliveredAt(),
                orderEntity.getCancelledAt());
    }

    private List<OrderItem> toDomainOrderItem(OrderEntity orderEntity) {
        var items = orderEntity.getItems();
        return items.stream()
                .map(item -> new OrderItem(
                        item.getId(),
                        orderEntity.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPriceCents(),
                        item.getLineTotalCents()))
                .toList();
    }
}
