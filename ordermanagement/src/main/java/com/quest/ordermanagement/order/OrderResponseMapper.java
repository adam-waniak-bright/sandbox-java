package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.model.OrderItemResponse;
import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.domain.OrderItem;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMapper {
    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .items(order.getItems().stream().map(this::toOrderItemResponse).toList())
                .subtotalCents(order.getSubtotalAmount())
                .taxCents(order.getTaxAmount())
                .shippingCents(order.getShippingAmount())
                .totalCents(order.getTotalAmount())
                .createdAt(toOffsetDateTime(order.getCreatedAt()))
                .confirmedAt(toOffsetDateTime(order.getConfirmedAt()))
                .shippedAt(toOffsetDateTime(order.getShippedAt()))
                .deliveredAt(toOffsetDateTime(order.getDeliveredAt()))
                .cancelledAt(toOffsetDateTime(order.getCancelledAt()));
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse().id(item.getId()).productId(item.getProductId());
    }
}
