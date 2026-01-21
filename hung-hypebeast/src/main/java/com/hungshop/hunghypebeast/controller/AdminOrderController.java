package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;
import com.hungshop.hunghypebeast.dto.response.OrderSummaryDto;
import com.hungshop.hunghypebeast.mapping.OrderMapper;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.entity.Payment;
import com.hungshop.hunghypebeast.exception.BusinessException;
import com.hungshop.hunghypebeast.exception.ResourceNotFoundException;
import com.hungshop.hunghypebeast.repository.OrderRepository;
import com.hungshop.hunghypebeast.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping
    public Page<OrderSummaryDto> listOrders(@RequestParam(value = "status", required = false) String status,
                                            Pageable pageable) {
        Page<Order> page;
        if (status != null && !status.isBlank()) {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            page = orderRepository.findByStatus(orderStatus, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }

        List<OrderSummaryDto> content = page.getContent().stream()
                .map(OrderMapper::toSummaryDto)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @GetMapping("/{orderId}")
    public OrderDetailDto getOrderDetail(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElse(null);

        String trackingToken = order.getTrackingToken() != null ? order.getTrackingToken().getToken() : null;
        return OrderMapper.toDetailDto(order, payment, trackingToken);
    }

    @PutMapping("/{orderId}/status")
    public OrderDetailDto updateStatus(@PathVariable Long orderId,
                                       @RequestParam("status") String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());

                if (order.getStatus() == Order.OrderStatus.CANCELLED && newStatus != Order.OrderStatus.CANCELLED) {
                        throw new BusinessException("Cannot change status of a CANCELLED order");
                }

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElse(null);

        String trackingToken = order.getTrackingToken() != null ? order.getTrackingToken().getToken() : null;
        return OrderMapper.toDetailDto(order, payment, trackingToken);
        }
}
