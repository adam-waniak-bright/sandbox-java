package com.acti.quest.order.service;

import com.acti.order.model.CreateOrderRequest;
import com.acti.order.model.OrderItemResponse;
import com.acti.order.model.OrderResponse;
import com.acti.quest.order.domain.Order;
import com.acti.quest.order.domain.OrderStatus;
import com.acti.quest.order.domain.OrderValidator;
import com.acti.quest.order.domain.OrderItem;
import com.acti.quest.order.repository.OrderItemRepository;
import com.acti.quest.order.repository.OrderRepository;
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
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderResponse handleCreateOrder(CreateOrderRequest request) {
        // Validate request and customer
        validator.validateRequest(request);
        customerService.validateCustomerIsActive(request.getCustomerId());

        // Build order items
        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            long lineTotal = itemReq.getQuantity() * itemReq.getUnitPriceCents();
            return new OrderItem(
                    UUID.randomUUID(),
                    null,  // orderId to be set after saving order
                    itemReq.getProductId(),
                    itemReq.getProductName(),
                    itemReq.getQuantity(),
                    itemReq.getUnitPriceCents(),
                    lineTotal
            );
        }).toList();

        // Calculate totals
        long subtotal = items.stream().mapToLong(OrderItem::getLineTotalCents).sum();
        long tax = Math.round(subtotal * Order.TAX_RATE);
        long shipping = subtotal < Order.SHIPPING_THRESHOLD_CENTS ? Order.SHIPPING_COST_CENTS : 0L;
        long total = subtotal + tax + shipping;

        if (total < Order.MIN_TOTAL_CENTS) {
            throw new IllegalArgumentException("Order total must be at least $1.00");
        }

        // Build order entity
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.DRAFT);
        order.setSubtotalAmount(subtotal);
        order.setTaxAmount(tax);
        order.setShippingAmount(shipping);
        order.setTotalAmount(total);
        order.setCreatedAt(OffsetDateTime.now());



        // Save order and items
        Order savedOrder = saveOrder(order, items);

        // Build and return OrderResponse
        return toOrderResponse(savedOrder);
    }

    private Order saveOrder(Order order, List<OrderItem> items) {
        Order savedOrder = orderRepository.save(order);
        items.forEach(item -> item.setOrder(savedOrder));  // ✅ set the full entity
        orderItemRepository.saveAll(items);
        savedOrder.setItems(items); // attach saved items
        return savedOrder;
    }


    private OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setSubtotalCents(Math.toIntExact(order.getSubtotalAmount()));
        response.setTaxCents(Math.toIntExact(order.getTaxAmount()));
        response.setShippingCents(Math.toIntExact(order.getShippingAmount()));
        response.setTotalCents(Math.toIntExact(order.getTotalAmount()));
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(
                order.getItems().stream().map(item -> {
                    var itemResponse = new com.acti.order.model.OrderItemResponse();
                    itemResponse.setId(UUID.fromString(String.valueOf(item.getId())));
                    itemResponse.setProductId(item.getProductId());
                    itemResponse.setProductName(item.getProductName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setUnitPriceCents(Math.toIntExact(item.getUnitPriceCents()));
                    return itemResponse;
                }).toList()
        );
        return response;
    }
}
