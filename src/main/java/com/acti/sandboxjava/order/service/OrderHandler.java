package com.acti.sandboxjava.order.service;


import com.acti.order.model.CreateOrderRequest;
import com.acti.sandboxjava.order.domain.OrderStatus;
import com.acti.sandboxjava.order.domain.OrderValidator;
import com.acti.sandboxjava.order.domain.Order;
import com.acti.sandboxjava.order.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderHandler {
    private final OrderValidator validator;
    private final CustomerService customerService;

    public Order handleCreateOrder(CreateOrderRequest request) {
        // Validate product IDs, quantities, prices
        validator.validateRequest(request);

        // Validate customer status
        customerService.validateCustomerIsActive(request.getCustomerId());

        // Calculate items line total
        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            long lineTotal = itemReq.getQuantity() * itemReq.getUnitPriceCents();
            return new OrderItem(UUID.randomUUID().toString(), null, itemReq.getProductId(),
                    itemReq.getProductName(), itemReq.getQuantity(),
                    itemReq.getUnitPriceCents(), lineTotal);
        }).toList();

        Result result = getResult(items);

        return getOrder(request, items, result.subtotal(), result.tax(), result.shipping(), result.total());
    }

    private Result getResult(List<OrderItem> items) {
        long subtotal = items.stream().mapToLong(OrderItem::getLineTotalCents).sum();
        long tax = Math.round(subtotal * Order.TAX_RATE);
        long shipping = subtotal < Order.SHIPPING_THRESHOLD_CENTS ? Order.SHIPPING_COST_CENTS : 0L;
        long total = subtotal + tax + shipping;

        if (total < Order.MIN_TOTAL_CENTS) {
            throw new IllegalArgumentException("Order total must be at least $1.00");
        }
        return new Result(subtotal, tax, shipping, total);
    }

    private record Result(long subtotal, long tax, long shipping, long total) {
    }

    private Order getOrder(CreateOrderRequest request, List<OrderItem> items, long subtotal, long tax, long shipping, long total) {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.DRAFT);
        order.setItems(items);
        order.setSubtotalAmount(subtotal);
        order.setTaxAmount(tax);
        order.setShippingAmount(shipping);
        order.setTotalAmount(total);
        order.setCreatedAt(OffsetDateTime.now());
        return order;
    }
}

