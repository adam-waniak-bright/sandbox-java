package com.acti.sandboxjava.order;

import com.acti.sandboxjava.api.model.CreateOrderItemRequest;
import com.acti.sandboxjava.api.model.CreateOrderRequest;
import com.acti.sandboxjava.api.model.OrderItemResponse;
import com.acti.sandboxjava.api.model.OrderListResponse;
import com.acti.sandboxjava.api.model.OrderResponse;
import com.acti.sandboxjava.api.model.OrderStatus;
import com.acti.sandboxjava.api.model.PaginationResponse;
import com.acti.sandboxjava.api.model.UpdateOrderStatusRequest;
import com.acti.sandboxjava.domain.Order;
import com.acti.sandboxjava.domain.OrderItem;
import com.acti.sandboxjava.domain.repo.OrderEntity;
import com.acti.sandboxjava.domain.repo.OrderItemEntity;
import com.acti.sandboxjava.domain.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderHandler {

    private final OrderRepository orderRepository;

    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must have at least 1 item");
        }

        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(request.getCustomerId());
        entity.setStatus(OrderStatus.DRAFT);
        entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        List<OrderItemEntity> itemEntities = new ArrayList<>();
        long subtotal = 0;

        for (CreateOrderItemRequest item : request.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setProductId(item.getProductId());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPriceCents(item.getUnitPriceCents());

            itemEntity.setOrder(entity); // back-reference
            itemEntities.add(itemEntity);

            subtotal += item.getQuantity() * item.getUnitPriceCents();
        }

        entity.setItems(itemEntities);
        entity.setSubtotalCents(subtotal);
        entity.setTaxCents(0L); // Placeholder
        entity.setShippingCents(0L); // Placeholder
        entity.setTotalCents(subtotal + 100); // Example rule: $1 min fee

        OrderEntity saved = orderRepository.save(entity);

        return toOrderResponse(saved);
    }

    private OrderResponse toOrderResponse(OrderEntity entity) {
        OrderResponse response = new OrderResponse();
        response.setId(entity.getId());
        response.setCustomerId(entity.getCustomerId());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setSubtotalCents(entity.getSubtotalCents());
        response.setShippingCents(entity.getShippingCents());
        response.setTaxCents(entity.getTaxCents());
        response.setTotalCents(entity.getTotalCents());

        if (entity.getConfirmedAt() != null) {
            response.setConfirmedAt(JsonNullable.of(entity.getConfirmedAt()));
        }
        if (entity.getShippedAt() != null) {
            response.setShippedAt(JsonNullable.of(entity.getShippedAt()));
        }
        if (entity.getDeliveredAt() != null) {
            response.setDeliveredAt(JsonNullable.of(entity.getDeliveredAt()));
        }
        if (entity.getCancelledAt() != null) {
            response.setCancelledAt(JsonNullable.of(entity.getCancelledAt()));
        }

        List<OrderItemResponse> items = new ArrayList<>();
        if (entity.getItems() != null) {
            for (OrderItemEntity item : entity.getItems()) {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setId(item.getId());
                ir.setProductId(item.getProductId());
                ir.setProductName(item.getProductName());
                ir.setQuantity(item.getQuantity());
                ir.setUnitPriceCents(item.getUnitPriceCents());
                items.add(ir);
            }
        }
        response.setItems(items);
        return response;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> {
                    OrderItemResponse itemResp = new OrderItemResponse();
                    itemResp.setId(item.getId());
                    itemResp.setProductId(item.getProductId());
                    itemResp.setProductName(item.getProductName());
                    itemResp.setQuantity(item.getQuantity());
                    itemResp.setUnitPriceCents(item.getUnitPriceCents());
                    return itemResp;
                })
                .toList();

        OrderResponse response = new OrderResponse();
        response.setId(order.getId()); // UUID assumed
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setItems(items);

        Long subtotal = (long) order.getItems().stream()
                .mapToInt(i -> Math.toIntExact(i.getUnitPriceCents()) * i.getQuantity())
                .sum();
        response.setSubtotalCents(subtotal);

        response.setTaxCents(order.getTaxAmount());
        response.setShippingCents(order.getShippingAmount());
        response.setTotalCents(order.getTotalAmount());

        response.setCreatedAt(order.getCreatedAt());

        response.setConfirmedAt(JsonNullable.of(order.getConfirmedAt()));
        response.setShippedAt(JsonNullable.of(order.getShippedAt()));
        response.setDeliveredAt(JsonNullable.of(order.getDeliveredAt()));
        response.setCancelledAt(JsonNullable.of(order.getCancelledAt()));


        return response;
    }

    public OrderListResponse listOrders(String customerId, OrderStatus status, Integer page, Integer limit) {
        int currentPage = (page != null && page > 0) ? page : 1;
        int pageSize = (limit != null && limit > 0) ? limit : 10;
        int offset = (currentPage - 1) * pageSize;

        // Step 1: Fetch entities from DB
        List<OrderEntity> entities = orderRepository.findByCustomerId(customerId);

        // Step 2: Convert to domain model
        List<Order> domainOrders = entities.stream()
                .map(this::mapToDomainOrder)
                .filter(order -> status == null || order.getStatus().equals(status))
                .toList();

        int totalItems = domainOrders.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        boolean hasNext = currentPage < totalPages;
        boolean hasPrevious = currentPage > 1;

        List<OrderResponse> pagedOrderResponses = entities.stream()
                .map(this::mapToDomainOrder)
                .filter(order -> status == null || order.getStatus().equals(status))
                .skip(offset)
                .limit(pageSize)
                .map(this::mapToOrderResponse)
                .toList();

        PaginationResponse pagination = new PaginationResponse()
                .page(currentPage)
                .limit(pageSize)
                .totalItems(totalItems)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious);

        return new OrderListResponse()
                .orders(pagedOrderResponses)
                .pagination(pagination);
    }
    private Order mapToDomainOrder(OrderEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());
        order.setCustomerId(entity.getCustomerId());
        order.setStatus(entity.getStatus());
        order.setCreatedAt(entity.getCreatedAt());
        order.setConfirmedAt(entity.getConfirmedAt());
        order.setShippedAt(entity.getShippedAt());
        order.setDeliveredAt(entity.getDeliveredAt());
        order.setCancelledAt(entity.getCancelledAt());
        order.setTaxAmount(entity.getTaxCents());
        order.setShippingAmount(entity.getShippingCents());
        order.setTotalAmount(entity.getTotalCents());
        order.setItems(
                entity.getItems().stream()
                        .map(this::mapToDomainItem)
                        .toList()
        );
        return order;
    }
    private OrderItem mapToDomainItem(OrderItemEntity entity) {
        OrderItem item = new OrderItem();
        item.setId(entity.getId());
        item.setProductId(entity.getProductId());
        item.setProductName(entity.getProductName());
        item.setQuantity(entity.getQuantity());
        item.setUnitPriceCents(entity.getUnitPriceCents());
        return item;
    }


    public OrderResponse getOrder(UUID orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return toOrderResponse(entity);
    }

    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        OrderStatus newStatus = updateOrderStatusRequest.getStatus();
        if (newStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status must be provided");
        }

        entity.setStatus(newStatus);
        entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        if (newStatus == OrderStatus.CONFIRMED) {
            entity.setConfirmedAt(entity.getConfirmedAt());
        } else if (newStatus == OrderStatus.SHIPPED) {
            entity.setShippedAt(entity.getShippedAt());
        } else if (newStatus == OrderStatus.DELIVERED) {
            entity.setDeliveredAt(entity.getDeliveredAt());
        } else if (newStatus == OrderStatus.CANCELLED) {
            entity.setCancelledAt(entity.getCancelledAt());
        }

        OrderEntity updatedEntity = orderRepository.save(entity);
        return toOrderResponse(updatedEntity);
    }
}
