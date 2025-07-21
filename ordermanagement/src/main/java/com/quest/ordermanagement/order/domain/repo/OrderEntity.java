package com.quest.ordermanagement.order.domain.repo;

import com.quest.ordermanagement.order.api.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OrderEntity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    private String id;

    private String customerId;
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    private Long subtotalAmount;
    private Long taxAmount;
    private Long shippingAmount;
    private Long totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
}
