package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.model.OrderStatus;
import java.util.Optional;

public record OrderFilter(Optional<String> customerId, Optional<OrderStatus> orderStatus) {
    public OrderFilter {
        customerId = customerId.filter(s -> !s.isBlank());
    }
}
