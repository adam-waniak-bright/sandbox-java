package com.acti.sandboxjava.domain.repo;

import com.acti.sandboxjava.api.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderEntity {
    @Id
    @GeneratedValue
    private UUID id;

    public List<OrderItemEntity> getItems() {
        return items;
    }

    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Long subtotalCents;
    private Long taxCents;
    private Long shippingCents;
    private Long totalCents;

    private OffsetDateTime createdAt;
    private OffsetDateTime confirmedAt;
    private OffsetDateTime shippedAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime cancelledAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Long getSubtotalCents() {
        return subtotalCents;
    }

    public Long getTaxCents() {
        return taxCents;
    }

    public Long getShippingCents() {
        return shippingCents;
    }

    public Long getTotalCents() {
        return totalCents;
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setSubtotalCents(Long subtotalCents) {
        this.subtotalCents = subtotalCents;
    }

    public void setTaxCents(Long taxCents) {
        this.taxCents = taxCents;
    }

    public void setShippingCents(Long shippingCents) {
        this.shippingCents = shippingCents;
    }

    public void setTotalCents(Long totalCents) {
        this.totalCents = totalCents;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setConfirmedAt(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setShippedAt(OffsetDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public void setDeliveredAt(OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setCancelledAt(OffsetDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }
}
