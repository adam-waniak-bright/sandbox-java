package com.quest.ordermanagement.order.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItem {
    private final String id;
    private String orderId;
    private final String productId;
    private final String productName;
    private final Integer quantity;
    private final Long unitPriceCents;
    private final Long lineTotalCents;

    public OrderItem(String productId, String productName, Integer quantity, Long unitPriceCents) {
        this.id = UUID.randomUUID().toString();
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
        this.lineTotalCents = quantity * unitPriceCents;
    }
}
