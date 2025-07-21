package com.quest.ordermanagement.order.domain.repo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private String productId;
    private String productName;
    private Integer quantity;
    private Long unitPriceCents;
    private Long lineTotalCents;
}
