package com.acti.quest.order.service;

import com.acti.order.model.OrderListResponse;
import com.acti.order.model.OrderResponse;
import com.acti.order.model.OrderStatus;
import com.acti.order.model.PaginationResponse;
import com.acti.quest.order.domain.Order;
import com.acti.quest.order.domain.OrderFilter;
import com.acti.quest.order.error.OrderNotFoundException;
import com.acti.quest.order.repo.OrderEntity;
import com.acti.quest.order.repo.OrderEntityMapper;
import com.acti.quest.order.repo.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchOrderHandler {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderResponseMapper orderResponseMapper;

    @SneakyThrows
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository
                .findById(orderId)
                .map(orderEntityMapper::toDomain)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        return orderResponseMapper.toOrderResponse(order);
    }

    @SneakyThrows
    public OrderListResponse listOrders(String customerId, OrderStatus status, Integer page, Integer limit) {

        int pageNum = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0 && limit <= 100) ? limit : 20;

        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        var filter = new OrderFilter(Optional.ofNullable(customerId), Optional.ofNullable(status));

        var specification = createOrderSpecification(filter);
        Page<Order> ordersPage =
                orderRepository.findAll(specification, pageable).map(orderEntityMapper::toDomain);
        return toOrderListResponse(ordersPage);
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

    @SneakyThrows
    private OrderListResponse toOrderListResponse(Page<Order> orderPage) {
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderResponseMapper::toOrderResponse)
                .toList();

        PaginationResponse pagination = new PaginationResponse();
        pagination.setPage(orderPage.getNumber() + 1);
        pagination.setLimit(orderPage.getSize());
        pagination.setTotalItems((int) orderPage.getTotalElements());
        pagination.setTotalPages(orderPage.getTotalPages());
        pagination.setHasNext(orderPage.hasNext());
        pagination.setHasPrevious(orderPage.hasPrevious());

        OrderListResponse response = new OrderListResponse();
        response.setOrders(orderResponses);
        response.setPagination(pagination);
        return response;
    }
}
