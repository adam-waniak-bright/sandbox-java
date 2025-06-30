package com.acti.sandboxjava.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private String customerId;  // Format: "CUST" + 4-8 alphanumeric
    private OrderStatus status;
    private List<OrderItem> items;
    private Long subtotalAmount;  // Amount in cents (e.g., $10.50 = 1050)
    private Long taxAmount;       // Tax amount in cents
    private Long shippingAmount;  // Shipping amount in cents
    private Long totalAmount;     // Total amount in cents
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    // Business logic constraints
    public static final int MIN_ITEMS = 1;
    public static final int MAX_ITEMS = 20;
    public static final long MIN_TOTAL_CENTS = 100L;  // $1.00
    public static final double TAX_RATE = 0.08;
    public static final long SHIPPING_THRESHOLD_CENTS = 5000L;  // $50.00
    public static final long SHIPPING_COST_CENTS = 500L;        // $5.00

    // Constructors, getters, setters...
}
