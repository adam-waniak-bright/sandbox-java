package com.acti.quest.order.service;

import com.acti.order.model.CreateOrderRequest;
import com.acti.order.model.OrderItemResponse;
import com.acti.order.model.OrderResponse;
import com.acti.quest.customer.service.CustomerService;
import com.acti.quest.order.domain.Order;
import com.acti.quest.order.domain.OrderItem;
import com.acti.quest.order.domain.OrderStatus;
import com.acti.quest.order.domain.OrderValidator;
import com.acti.quest.order.repo.OrderEntity;
import com.acti.quest.order.repo.OrderEntityMapper;
import com.acti.quest.order.repo.OrderRepository;
import java.time.ZoneOffset;
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
public class CreateOrderHandler {

    private final OrderValidator validator;
    private final CustomerService customerService;
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    @SneakyThrows
    public OrderResponse handleCreateOrder(CreateOrderRequest createOrderRequest) {

        validator.validateRequest(createOrderRequest);
        customerService.validateCustomerIsActive(createOrderRequest.getCustomerId());

        List<OrderItem> orderItems = createOrderItems(createOrderRequest);

        Order order = new Order(createOrderRequest.getCustomerId(), orderItems, OrderStatus.DRAFT);

        OrderEntity savedEntity = orderRepository.save(orderEntityMapper.toEntity(order));
        Order savedDomainOrder = orderEntityMapper.toDomain(savedEntity);
        return orderResponseMapper.toOrderResponse(savedDomainOrder);
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


}
