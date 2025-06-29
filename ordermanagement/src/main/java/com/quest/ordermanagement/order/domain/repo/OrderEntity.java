package com.quest.ordermanagement.order.domain.repo;

import com.quest.ordermanagement.order.api.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderEntity {
    private String id;
    private String customerId;
    private OrderStatus status;
    private List<OrderItemEntity> items;
    private Long subtotalAmount;
    private Long taxAmount;
    private Long shippingAmount;
    private Long totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
}
