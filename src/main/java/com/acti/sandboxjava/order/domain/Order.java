package com.acti.sandboxjava.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(name = "customer_id", nullable = false, length = 20)
    private String customerId;  // Format: "CUST" + 4-8 alphanumeric

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Column(name = "subtotal_cents", nullable = false)
    private Long subtotalAmount;

    @Column(name = "tax_cents", nullable = false)
    private Long taxAmount;

    @Column(name = "shipping_cents", nullable = false)
    private Long shippingAmount;

    @Column(name = "total_cents", nullable = false)
    private Long totalAmount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Column(name = "shipped_at")
    private OffsetDateTime shippedAt;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    // Business logic constraints
    public static final int MIN_ITEMS = 1;
    public static final int MAX_ITEMS = 20;
    public static final long MIN_TOTAL_CENTS = 100L;
    public static final double TAX_RATE = 0.08;
    public static final long SHIPPING_THRESHOLD_CENTS = 5000L;
    public static final long SHIPPING_COST_CENTS = 500L;

    // Getters & setters, constructors, equals & hashCode if needed
}
