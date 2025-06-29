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
                toEntityOrderItem(order.getItems()),
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

    private List<OrderItemEntity> toEntityOrderItem(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemEntity(
                        item.getId(),
                        item.getOrderId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPriceCents(),
                        item.getLineTotalCents()))
                .toList();
    }

    public Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getStatus(),
                toDomainOrderItem(entity.getItems()),
                entity.getSubtotalAmount(),
                entity.getTaxAmount(),
                entity.getShippingAmount(),
                entity.getTotalAmount(),
                entity.getCreatedAt(),
                entity.getConfirmedAt(),
                entity.getShippedAt(),
                entity.getDeliveredAt(),
                entity.getCancelledAt());
    }

    private List<OrderItem> toDomainOrderItem(List<OrderItemEntity> items) {
        return items.stream()
                .map(item -> new OrderItem(
                        item.getId(),
                        item.getOrderId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPriceCents(),
                        item.getLineTotalCents()))
                .toList();
    }
}
