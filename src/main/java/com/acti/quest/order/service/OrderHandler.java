package com.acti.quest.order.service;

import com.acti.order.model.CreateOrderRequest;
import com.acti.order.model.OrderResponse;
import com.acti.quest.customer.service.CustomerService;
import com.acti.quest.order.domain.Order;
import com.acti.quest.order.domain.OrderItem;
import com.acti.quest.order.domain.OrderStatus;
import com.acti.quest.order.domain.OrderValidator;
import com.acti.quest.order.repo.OrderEntity;
import com.acti.quest.order.repo.OrderEntityMapper;
import com.acti.quest.order.repo.OrderRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final OrderValidator validator;
    private final CustomerService customerService;
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;

    @SneakyThrows
    public OrderResponse handleCreateOrder(CreateOrderRequest createOrderRequest) {

        validator.validateRequest(createOrderRequest);
        customerService.validateCustomerIsActive(createOrderRequest.getCustomerId());

        List<OrderItem> orderItems = createOrderItems(createOrderRequest);

        Order order = new Order(createOrderRequest.getCustomerId(), orderItems, OrderStatus.DRAFT);

        OrderEntity savedEntity = orderRepository.save(orderEntityMapper.toEntity(order));
        Order savedDomainOrder = orderEntityMapper.toDomain(savedEntity);
        return toOrderResponse(savedDomainOrder);
    }

    private List<OrderItem> createOrderItems(CreateOrderRequest createOrderRequest) {
        Set<String> productIds = new HashSet<>();
        List<OrderItem> orderItems = new ArrayList<>();

        for (var item : createOrderRequest.getItems()) {
            // validate product ID uniqueness
            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException("Duplicate product ID found: " + item.getProductId());
            }
            orderItems.add(new OrderItem(
                    UUID.randomUUID().toString(),
                    null, // orderId is set when saving / mapping
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPriceCents(),
                    item.getQuantity() * item.getUnitPriceCents()));
        }
        return orderItems;
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(UUID.fromString(order.getId()));
        response.setCustomerId(order.getCustomerId());
        response.setSubtotalCents(Math.toIntExact(order.getSubtotalAmount()));
        response.setTaxCents(Math.toIntExact(order.getTaxAmount()));
        response.setShippingCents(Math.toIntExact(order.getShippingAmount()));
        response.setTotalCents(Math.toIntExact(order.getTotalAmount()));
        response.setCreatedAt(OffsetDateTime.from(order.getCreatedAt()));
        response.setItems(order.getItems().stream()
                .map(item -> {
                    var itemResponse = new com.acti.order.model.OrderItemResponse();
                    itemResponse.setId(UUID.fromString(String.valueOf(item.getId())));
                    itemResponse.setProductId(item.getProductId());
                    itemResponse.setProductName(item.getProductName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setUnitPriceCents(Math.toIntExact(item.getUnitPriceCents()));
                    return itemResponse;
                })
                .toList());
        return response;
    }
}
