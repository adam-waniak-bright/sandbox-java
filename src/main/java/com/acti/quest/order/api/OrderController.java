package com.acti.quest.order.api;

import com.acti.order.api.OrdersApi;
import com.acti.order.model.CreateOrderRequest;
import com.acti.order.model.OrderListResponse;
import com.acti.order.model.OrderResponse;
import com.acti.order.model.OrderStatus;
import com.acti.order.model.UpdateOrderStatusRequest;
import com.acti.quest.order.service.CreateOrderHandler;
import com.acti.quest.order.service.FetchOrderHandler;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

    private final CreateOrderHandler createOrderHandler;
    private final FetchOrderHandler fetchOrderHandler;

    @Override
    public ResponseEntity<OrderResponse> createOrder(@Valid CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(createOrderHandler.handleCreateOrder(createOrderRequest));
    }

    @Override
    public ResponseEntity<OrderResponse> getOrder(UUID orderId) {
        return ResponseEntity.ok(fetchOrderHandler.getOrderById(orderId));
    }

    @Override
    public ResponseEntity<OrderListResponse> listOrders(
            String customerId, OrderStatus status, Integer page, Integer limit) {
        // TODO: Replace this stub with actual implementation
        OrderListResponse response = new OrderListResponse();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrderResponse> updateOrderStatus(
            UUID orderId, @Valid UpdateOrderStatusRequest updateOrderStatusRequest) {
        // TODO: Replace this stub with actual implementation
        OrderResponse response = new OrderResponse();
        return ResponseEntity.ok(response);
    }
}
