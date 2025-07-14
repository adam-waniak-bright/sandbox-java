package com.acti.quest.order.service;

import com.acti.order.model.OrderItemResponse;
import com.acti.order.model.OrderResponse;
import com.acti.quest.order.domain.Order;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
public class OrderResponseMapper {

    @SneakyThrows
    OrderResponse toOrderResponse(Order order) {
        return new OrderResponse()
                .id(UUID.fromString(order.getId()))
                .customerId(order.getCustomerId())
                .status(com.acti.order.model.OrderStatus.valueOf(
                        order.getStatus().name()))
                .items(getOrderItemResponses(order))
                .subtotalCents(order.getSubtotalAmount().intValue())
                .taxCents(order.getTaxAmount().intValue())
                .shippingCents(order.getShippingAmount().intValue())
                .totalCents(order.getTotalAmount().intValue())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().atOffset(ZoneOffset.UTC) : null)
                .confirmedAt(
                        order.getConfirmedAt() != null ? order.getConfirmedAt().atOffset(ZoneOffset.UTC) : null)
                .shippedAt(order.getShippedAt() != null ? order.getShippedAt().atOffset(ZoneOffset.UTC) : null)
                .deliveredAt(
                        order.getDeliveredAt() != null ? order.getDeliveredAt().atOffset(ZoneOffset.UTC) : null)
                .cancelledAt(
                        order.getCancelledAt() != null ? order.getCancelledAt().atOffset(ZoneOffset.UTC) : null);
    }

    List<OrderItemResponse> getOrderItemResponses(Order order) {
        return order.getItems().stream()
                .map(item -> {
                    var itemResponse = new OrderItemResponse();
                    itemResponse.setId(UUID.fromString(String.valueOf(item.getId())));
                    itemResponse.setProductId(item.getProductId());
                    itemResponse.setProductName(item.getProductName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setUnitPriceCents(Math.toIntExact(item.getUnitPriceCents()));
                    return itemResponse;
                })
                .toList();
    }
}
