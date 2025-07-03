package com.acti.quest.order.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Order {
    private String id;
    private String customerId;
    private OrderStatus status;
    private List<OrderItem> items;
    private Long subtotalAmount;
    private Long taxAmount;
    private Long shippingAmount;
    private Long totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    private static final double TAX_RATE = 0.08;
    private static final long FREE_SHIPPING_THRESHOLD = 5000L;
    private static final long SHIPPING_FEE = 500L;
    private static final long MINIMUM_TOTAL_AMOUNT = 100L;

    public Order(String customerId, List<OrderItem> items, OrderStatus status) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.status = status;
        this.items = items;
        this.subtotalAmount = calculateSubtotal(items);
        this.taxAmount = calculateTax(subtotalAmount);
        this.shippingAmount = calculateShipping(subtotalAmount);
        this.totalAmount = subtotalAmount + taxAmount + shippingAmount;

        if (totalAmount < MINIMUM_TOTAL_AMOUNT) {
            throw new IllegalArgumentException("Total amount must be at least $1.00");
        }

        this.createdAt = LocalDateTime.now();
    }

    private Long calculateSubtotal(List<OrderItem> items) {
        return items.stream().mapToLong(OrderItem::getLineTotalCents).sum();
    }

    private Long calculateTax(Long subtotalAmount) {
        return Math.round(subtotalAmount * TAX_RATE);
    }

    private Long calculateShipping(Long subtotalAmount) {
        return (subtotalAmount < FREE_SHIPPING_THRESHOLD) ? SHIPPING_FEE : 0L;
    }
}
