package com.acti.sandboxjava.domain;

import com.acti.sandboxjava.api.model.OrderStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public class Order {
    private UUID id;
    private String customerId;  // Format: "CUST" + 4-8 alphanumeric
    private OrderStatus status;
    private List<OrderItem> items;
    private Long subtotalAmount;  // Amount in cents (e.g., $10.50 = 1050)
    private Long taxAmount;       // Tax amount in cents
    private Long shippingAmount;  // Shipping amount in cents
    private Long totalAmount;     // Total amount in cents
    private OffsetDateTime createdAt;
    private OffsetDateTime confirmedAt;
    private OffsetDateTime shippedAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime cancelledAt;

    // Business logic constraints
    public static final int MIN_ITEMS = 1;
    public static final int MAX_ITEMS = 20;
    public static final long MIN_TOTAL_CENTS = 100L;  // $1.00
    public static final double TAX_RATE = 0.08;
    public static final long SHIPPING_THRESHOLD_CENTS = 5000L;  // $50.00
    public static final long SHIPPING_COST_CENTS = 500L;

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Long getSubtotalAmount() {
        return subtotalAmount;
    }

    public Long getTaxAmount() {
        return taxAmount;
    }

    public Long getShippingAmount() {
        return shippingAmount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public OffsetDateTime getShippedAt() {
        return shippedAt;
    }

    public OffsetDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(OffsetDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setDeliveredAt(OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setShippedAt(OffsetDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public void setConfirmedAt(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setShippingAmount(Long shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public void setTaxAmount(Long taxAmount) {
        this.taxAmount = taxAmount;
    }

    public void setSubtotalAmount(Long subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
