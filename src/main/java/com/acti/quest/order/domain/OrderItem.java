package com.acti.quest.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderItem {
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private Long unitPriceCents;
    private Long lineTotalCents;
}
