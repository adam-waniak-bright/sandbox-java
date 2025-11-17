package com.quest.ordermanagement.order.query;

import com.quest.ordermanagement.order.api.model.OrderStatus;

public record FindOrdersQuery(String customerId, OrderStatus status, int page, int limit) {}
