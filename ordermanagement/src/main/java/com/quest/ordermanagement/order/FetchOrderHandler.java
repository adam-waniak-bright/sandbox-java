package com.quest.ordermanagement.order;

import com.quest.ordermanagement.order.api.model.OrderListResponse;
import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.api.model.PaginationResponse;
import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.domain.repo.OrderEntity;
import com.quest.ordermanagement.order.domain.repo.OrderEntityMapper;
import com.quest.ordermanagement.order.domain.repo.OrderRepository;
import com.quest.ordermanagement.order.error.OrderNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchOrderHandler {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    public OrderResponse getOrder(String orderId) {
        return orderRepository
                .findById(orderId)
                .map(orderEntityMapper::toDomain)
                .map(orderResponseMapper::toOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    public OrderListResponse listOrders(String customerId, OrderStatus status, Integer page, Integer limit) {
        log.info(
                "Fetching orders for customerId: {}, status: {}, page: {}, limit: {}", customerId, status, page, limit);
        var pageable = PageRequest.of(Math.max(0, page - 1), limit);
        var specification =
                createOrderSpecification(new OrderFilter(Optional.ofNullable(customerId), Optional.ofNullable(status)));
        Page<Order> orderPage = orderRepository.findAll(specification, pageable).map(orderEntityMapper::toDomain);
        return mapToOrderListResponse(orderPage);
    }

    private OrderListResponse mapToOrderListResponse(Page<Order> orderPage) {
        return new OrderListResponse()
                .orders(orderPage.getContent().stream()
                        .map(orderResponseMapper::toOrderResponse)
                        .toList())
                .pagination(new PaginationResponse()
                        .page(orderPage.getNumber())
                        .limit(orderPage.getSize())
                        .totalItems((int) orderPage.getTotalElements())
                        .totalPages(orderPage.getTotalPages())
                        .hasNext(orderPage.hasNext())
                        .hasPrevious(orderPage.hasPrevious()));
    }

    private Specification<OrderEntity> createOrderSpecification(OrderFilter filter) {
        Specification<OrderEntity> spec = (root, query, builder) -> builder.conjunction();
        if (filter.customerId().isPresent()) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(root.get("customerId"), filter.customerId().get()));
        }
        if (filter.orderStatus().isPresent()) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(root.get("status"), filter.orderStatus().get().name()));
        }
        return spec;
    }
}
