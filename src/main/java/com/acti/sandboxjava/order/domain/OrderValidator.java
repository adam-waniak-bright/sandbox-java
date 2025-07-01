package com.acti.sandboxjava.order.domain;

import com.acti.order.model.CreateOrderRequest;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OrderValidator {
    public void validateRequest(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty() || request.getItems().size() > Order.MAX_ITEMS) {
            throw new IllegalArgumentException("Order must have between 1 and 20 items");
        }

        Set<String> productIds = new HashSet<>();
        for (var item : request.getItems()) {
            if (item.getQuantity() < OrderItem.MIN_QUANTITY || item.getQuantity() > OrderItem.MAX_QUANTITY) {
                throw new IllegalArgumentException("Invalid quantity for product: " + item.getProductId());
            }
            if (item.getUnitPriceCents() < OrderItem.MIN_UNIT_PRICE_CENTS || item.getUnitPriceCents() > OrderItem.MAX_UNIT_PRICE_CENTS) {
                throw new IllegalArgumentException("Invalid unit price for product: " + item.getProductId());
            }
            if (!item.getProductId().matches("^PRD\\d{5}$")) {
                throw new IllegalArgumentException("Invalid product ID: " + item.getProductId());
            }
            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException("Duplicate product ID: " + item.getProductId());
            }
        }
    }
}

