package com.acti.quest.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
public class OrderItem {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false, length = 20)
    private String productId;  // Format: "PRD" + 5 digits

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price_cents", nullable = false)
    private Long unitPriceCents;

    @Column(name = "line_total_cents", nullable = false)
    private Long lineTotalCents;

    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 99;
    public static final long MIN_UNIT_PRICE_CENTS = 1L;
    public static final long MAX_UNIT_PRICE_CENTS = 999999L;

    // Getters & setters, constructors, equals & hashCode if needed
}
