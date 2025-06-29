package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.OrdersApi;
import com.quest.ordermanagement.order.api.model.CreateOrderRequest;
import com.quest.ordermanagement.order.api.model.OrderListResponse;
import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.api.model.UpdateOrderStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {
    private final CreateOrderHandler createOrderHandler;
    private final FetchOrderHandler fetchOrderHandler;
    private final UpdateOrderHandler updateOrderHandler;

    @Override
    public ResponseEntity<OrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(createOrderHandler.createOrder(createOrderRequest));
    }

    @Override
    public ResponseEntity<OrderResponse> getOrder(String orderId) {
        return ResponseEntity.ok(fetchOrderHandler.getOrder(orderId));
    }

    @Override
    public ResponseEntity<OrderListResponse> listOrders(
            String customerId, OrderStatus status, Integer page, Integer limit) {
        return ResponseEntity.ok(fetchOrderHandler.listOrders(customerId, status, page, limit));
    }

    @Override
    public ResponseEntity<OrderResponse> updateOrderStatus(
            String orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        return ResponseEntity.ok(updateOrderHandler.updateOrderStatus(orderId, updateOrderStatusRequest));
    }
}
