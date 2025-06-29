package com.quest.ordermanagement.order;

import com.quest.ordermanagement.customer.FetchCustomerHandler;
import com.quest.ordermanagement.order.api.model.CreateOrderRequest;
import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.domain.OrderItem;
import com.quest.ordermanagement.order.domain.repo.OrderEntityMapper;
import com.quest.ordermanagement.order.domain.repo.OrderRepository;
import com.quest.ordermanagement.product.FetchProductHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderHandler {
    private final OrderRepository orderRepository;
    private final FetchCustomerHandler fetchCustomerHandler;
    private final FetchProductHandler fetchProductHandler;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        fetchCustomerHandler.verifyCustomerExists(createOrderRequest.getCustomerId());
        var orderItems = createOrderItems(createOrderRequest);
        var order = new Order(createOrderRequest.getCustomerId(), orderItems, OrderStatus.DRAFT);
        orderRepository.save(orderEntityMapper.toEntity(order));
        return orderResponseMapper.toOrderResponse(order);
    }

    private List<OrderItem> createOrderItems(CreateOrderRequest createOrderRequest) {
        Set<String> productIds = new HashSet<>();
        List<OrderItem> orderItems = new ArrayList<>();
        for (var item : createOrderRequest.getItems()) {
            // validate product ID uniqueness
            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException("Duplicate product ID found: " + item.getProductId());
            }
            // validate product existence
            fetchProductHandler.verifyProductExists(item.getProductId());
            orderItems.add(new OrderItem(
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPriceCents().longValue()));
        }
        return orderItems;
    }
}
