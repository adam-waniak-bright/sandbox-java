package com.acti.quest.order.domain;

import com.acti.order.model.OrderStatus;
import java.util.Optional;

public record OrderFilter(Optional<String> customerId, Optional<OrderStatus> orderStatus) {}
