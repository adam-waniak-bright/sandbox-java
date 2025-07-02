package com.acti.sandboxjava.order;


import com.acti.sandboxjava.api.model.CreateOrderRequest;
import com.acti.sandboxjava.api.model.OrderListResponse;
import com.acti.sandboxjava.api.model.OrderResponse;
import com.acti.sandboxjava.api.model.OrderStatus;
import com.acti.sandboxjava.api.model.UpdateOrderStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {
    private final OrderHandler orderHandler;

    @Override
    public ResponseEntity<OrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
        OrderResponse response = orderHandler.createOrder(createOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<OrderResponse> getOrder(UUID orderId) {
        return ResponseEntity.ok( orderHandler.getOrder(orderId));
    }

    @Override
    public ResponseEntity<OrderListResponse> listOrders(String customerId, OrderStatus status, Integer page, Integer limit) {
        OrderListResponse response = orderHandler.listOrders(customerId, status, page, limit);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrderResponse> updateOrderStatus(UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        OrderResponse response = orderHandler.updateOrderStatus(orderId, updateOrderStatusRequest);
        return ResponseEntity.ok(response);
    }
}
