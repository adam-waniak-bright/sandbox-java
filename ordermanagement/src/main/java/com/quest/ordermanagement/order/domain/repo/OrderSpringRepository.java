package com.quest.ordermanagement.order.domain.repo;

import static com.quest.ordermanagement.order.domain.repo.OrderEntityMapper.toEntity;

import com.quest.ordermanagement.order.api.model.OrderStatus;
import com.quest.ordermanagement.order.domain.Order;
import com.quest.ordermanagement.order.error.OrderNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
class OrderSpringRepository implements OrderRepository {
    private final OrderEntityRepository orderEntityRepository;

    @Override
    public void saveOrder(Order order) {
        orderEntityRepository.save(toEntity(order));
    }

    @Override
    public Optional<Order> findOrderById(String orderId) {
        return orderEntityRepository.findById(orderId).map(OrderEntityMapper::toDomain);
    }

    @Override
    public Order getOrderById(String orderId) {
        return orderEntityRepository
                .findById(orderId)
                .map(OrderEntityMapper::toDomain)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    @Override
    public Page<Order> findAllOrders(String customerId, OrderStatus status, int page, int limit) {
        var pageable = PageRequest.of(Math.max(0, page - 1), limit);
        var specification = createOrderSpecification(new OrderFilter(customerId, Optional.ofNullable(status)));
        return orderEntityRepository.findAll(specification, pageable).map(OrderEntityMapper::toDomain);
    }

    private Specification<OrderEntity> createOrderSpecification(OrderFilter filter) {
        Specification<OrderEntity> spec =
                (root, query, builder) -> builder.equal(root.get("customerId"), filter.customerId());

        if (filter.orderStatus().isPresent()) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(root.get("status"), filter.orderStatus().get().name()));
        }
        return spec;
    }

    record OrderFilter(String customerId, Optional<OrderStatus> orderStatus) {}
}
