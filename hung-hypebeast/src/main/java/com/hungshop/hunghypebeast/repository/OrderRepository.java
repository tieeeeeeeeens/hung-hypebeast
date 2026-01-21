package com.hungshop.hunghypebeast.repository;

import com.hungshop.hunghypebeast.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = {"items", "items.variant", "items.variant.product"})
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"items", "items.variant", "items.variant.product"})
    Page<Order> findAll(Pageable pageable);

    List<Order> findByStatusAndCreatedAtBefore(Order.OrderStatus status, LocalDateTime createdAtBefore);
}
