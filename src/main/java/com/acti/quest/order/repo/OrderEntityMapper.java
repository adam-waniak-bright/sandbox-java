package com.acti.quest.order.repo;

import com.acti.quest.order.domain.Order;
import com.acti.quest.order.domain.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(UUID.fromString(order.getId()))
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .subtotalAmount(order.getSubtotalAmount())
                .taxAmount(order.getTaxAmount())
                .shippingAmount(order.getShippingAmount())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .confirmedAt(order.getConfirmedAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .build();

        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> OrderItemEntity.builder()
                        .id(UUID.fromString(item.getId()))
                        .order(orderEntity) // set the *parent* orderEntity here!
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPriceCents(item.getUnitPriceCents())
                        .lineTotalCents(item.getLineTotalCents())
                        .build())
                .toList();

        orderEntity.setItems(itemEntities);
        return orderEntity;
    }

    public Order toDomain(OrderEntity entity) {
        return new Order(
                String.valueOf(entity.getId()),
                entity.getCustomerId(),
                entity.getStatus(),
                getDomainItems(entity),
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

    private List<OrderItem> getDomainItems(OrderEntity entity) {
        var items = entity.getItems();
        return items.stream()
                .map(item -> new OrderItem(
                        String.valueOf(item.getId()),
                        String.valueOf(item.getOrder().getId()),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPriceCents(),
                        item.getLineTotalCents()))
                .toList();
    }
}
