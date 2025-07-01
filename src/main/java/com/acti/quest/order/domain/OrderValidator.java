package com.acti.quest.order.domain;

import com.acti.order.model.CreateOrderRequest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OrderValidator {

    @SneakyThrows
    public void validateRequest(CreateOrderRequest request) {
        Set<String> productIds = new HashSet<>();
        for (var item : request.getItems()) {
            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException("Duplicate product ID: " + item.getProductId());
            }
        }
    }
}

