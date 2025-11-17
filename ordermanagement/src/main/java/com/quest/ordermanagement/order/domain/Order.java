package com.quest.ordermanagement.order.domain;

import com.quest.ordermanagement.order.api.model.OrderStatus;
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

    /**
     * The tax rate applied to the order subtotal.
     * This is a constant value representing an 8% tax rate.
     */
    private static final double TAX_RATE = 0.08;
    /**
     * The threshold for free shipping.
     * If the order subtotal is below this amount, a shipping fee is applied.
     * If it is equal to or above this amount, shipping is free.
     * This value is in cents, so 5000L represents $50.00.
     */
    private static final long FREE_SHIPPING_THRESHOLD = 5000L;
    /**
     * The shipping fee applied to the order if the subtotal is below the free shipping threshold.
     * This value is in cents, so 500L represents $5.00.
     */
    private static final long SHIPPING_FEE = 500L;
    /**
     * The minimum total amount required for an order.
     * This value is in cents, so 100L represents $1.00.
     */
    private static final long MINIMUM_TOTAL_AMOUNT = 100L;

    private Order(String customerId, List<OrderItem> items) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.status = OrderStatus.DRAFT;
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

    private Long calculateShipping(Long subtotalAmount) {
        return (subtotalAmount < FREE_SHIPPING_THRESHOLD) ? SHIPPING_FEE : 0L;
    }

    private Long calculateTax(Long subtotalAmount) {
        return Math.round(subtotalAmount * TAX_RATE);
    }

    private Long calculateSubtotal(List<OrderItem> items) {
        return items.stream().mapToLong(OrderItem::getLineTotalCents).sum();
    }

    public static Order create(String customerId, List<OrderItem> items) {
        return new Order(customerId, items);
    }

    public void updateStatus(OrderStatus newStatus) {
        switch (newStatus) {
            case CONFIRMED:
                if (!(this.status == OrderStatus.DRAFT)) {
                    throw new IllegalArgumentException("Invalid status transition");
                }
                this.status = OrderStatus.CONFIRMED;
                this.confirmedAt = LocalDateTime.now();
                break;
            case SHIPPED:
                if (!(this.status == OrderStatus.CONFIRMED)) {
                    throw new IllegalArgumentException("Invalid status transition");
                }
                this.status = OrderStatus.SHIPPED;
                this.shippedAt = LocalDateTime.now();
                break;
            case DELIVERED:
                if (!(this.status == OrderStatus.SHIPPED)) {
                    throw new IllegalArgumentException("Invalid status transition");
                }
                this.status = OrderStatus.DELIVERED;
                this.deliveredAt = LocalDateTime.now();
                break;
            case CANCELLED:
                if (!(this.status == OrderStatus.DRAFT
                        || this.status == OrderStatus.CONFIRMED
                        || this.status == OrderStatus.SHIPPED)) {
                    throw new IllegalArgumentException("Invalid status transition");
                }
                this.status = OrderStatus.CANCELLED;
                this.cancelledAt = LocalDateTime.now();
                break;
            default:
                throw new IllegalArgumentException("Invalid status transition");
        }
    }
}
