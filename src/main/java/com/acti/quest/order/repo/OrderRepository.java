package com.acti.quest.order.repo;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findById(UUID id);

    Page<OrderEntity> findAll(Pageable pageable);
}
