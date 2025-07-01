package com.acti.quest.order.service;

import com.acti.order.model.CreateOrderRequest;
import com.acti.order.model.OrderItemResponse;
import com.acti.order.model.OrderResponse;
import com.acti.order.model.OrderStatus;
import com.acti.quest.order.domain.Order;
import com.acti.quest.order.domain.OrderValidator;
import com.acti.quest.order.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderHandler {
    private final OrderValidator validator;
    private final CustomerService customerService;

    @SneakyThrows
    public OrderResponse handleCreateOrder(CreateOrderRequest request) {
        // Validate product IDs, quantities, prices
        validator.validateRequest(request);

        // Validate customer status
        customerService.validateCustomerIsActive(request.getCustomerId());

        // Calculate items line totals
        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            long lineTotal = itemReq.getQuantity() * itemReq.getUnitPriceCents();
            return new OrderItem(
                    UUID.randomUUID(),
                    null,
                    itemReq.getProductId(),
                    itemReq.getProductName(),
                    itemReq.getQuantity(),
                    itemReq.getUnitPriceCents(),
                    lineTotal
            );
        }).toList();

        long subtotal = items.stream().mapToLong(OrderItem::getLineTotalCents).sum();
        long tax = Math.round(subtotal * Order.TAX_RATE);
        long shipping = subtotal < Order.SHIPPING_THRESHOLD_CENTS ? Order.SHIPPING_COST_CENTS : 0L;
        long total = subtotal + tax + shipping;

        if (total < Order.MIN_TOTAL_CENTS) {
            throw new IllegalArgumentException("Order total must be at least $1.00");
        }

        return buildOrderResponse(request.getCustomerId(), items, subtotal, tax, shipping, total);
    }

    private OrderResponse buildOrderResponse(String customerId, List<OrderItem> items, long subtotal, long tax, long shipping, long total) {
        OrderResponse response = new OrderResponse();
        response.setId(UUID.randomUUID());
        response.setCustomerId(customerId);
        response.setStatus(OrderStatus.DRAFT);
        response.setCreatedAt(OffsetDateTime.now());

        response.setSubtotalCents((int) subtotal);
        response.setTaxCents((int) tax);
        response.setShippingCents((int) shipping);
        response.setTotalCents((int) total);

        // Map OrderItem → OrderItemResponse
        List<OrderItemResponse> itemResponses = items.stream().map(item -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProductId());
            itemResponse.setProductName(item.getProductName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPriceCents(Math.toIntExact(item.getUnitPriceCents()));
            return itemResponse;
        }).toList();

        response.setItems(itemResponses);

        return response;
    }
}
