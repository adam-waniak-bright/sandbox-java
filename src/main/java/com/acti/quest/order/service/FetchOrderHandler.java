package com.acti.quest.order.service;

import com.acti.order.model.OrderResponse;
import com.acti.quest.order.domain.Order;
import com.acti.quest.order.error.OrderNotFoundException;
import com.acti.quest.order.repo.OrderEntityMapper;
import com.acti.quest.order.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FetchOrderHandler {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    @SneakyThrows
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .map(orderEntityMapper::toDomain)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        return orderResponseMapper.toOrderResponse(order);
    }

    @SneakyThrows
    public OrderResponse listOrders(String customerId, String status, Integer page, Integer limit) {

        Sort sort = Sort.by("paymentDate");
        sort = sort.ascending();


        Pageable pageable = PageRequest.of(0, limit, sort);


    }
}

