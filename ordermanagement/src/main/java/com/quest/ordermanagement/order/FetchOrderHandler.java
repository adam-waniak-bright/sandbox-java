package com.quest.ordermanagement.order;

import static com.quest.ordermanagement.order.OrderResponseMapper.toOrderResponse;

import com.quest.ordermanagement.order.api.model.OrderListResponse;
import com.quest.ordermanagement.order.api.model.OrderResponse;
import com.quest.ordermanagement.order.api.model.PaginationResponse;
import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.domain.repo.OrderRepository;
import com.quest.ordermanagement.order.query.FindOrdersQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchOrderHandler {
    private final OrderRepository orderRepository;

    public OrderResponse getOrder(String orderId) {
        return toOrderResponse(orderRepository.getOrderById(orderId));
    }

    public OrderListResponse listOrders(FindOrdersQuery query) {
        log.info(
                "Fetching orders for customerId: {}, status: {}, page: {}, limit: {}",
                query.customerId(),
                query.status(),
                query.page(),
                query.limit());
        Page<Order> orderPage =
                orderRepository.findAllOrders(query.customerId(), query.status(), query.page(), query.limit());
        return mapToOrderListResponse(orderPage);
    }

    private OrderListResponse mapToOrderListResponse(Page<Order> orderPage) {
        return new OrderListResponse()
                .orders(orderPage.getContent().stream()
                        .map(OrderResponseMapper::toOrderResponse)
                        .toList())
                .pagination(new PaginationResponse()
                        .page(orderPage.getNumber())
                        .limit(orderPage.getSize())
                        .totalItems((int) orderPage.getTotalElements())
                        .totalPages(orderPage.getTotalPages())
                        .hasNext(orderPage.hasNext())
                        .hasPrevious(orderPage.hasPrevious()));
    }
}
