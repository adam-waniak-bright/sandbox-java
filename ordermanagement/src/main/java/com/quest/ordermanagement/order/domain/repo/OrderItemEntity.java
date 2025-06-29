package com.quest.ordermanagement.order.domain.repo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Builder
@AllArgsConstructor
public class OrderItemEntity {
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private Long unitPriceCents;
    private Long lineTotalCents;
}
