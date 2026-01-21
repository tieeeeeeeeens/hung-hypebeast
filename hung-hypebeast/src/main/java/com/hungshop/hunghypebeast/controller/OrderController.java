package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;
import com.hungshop.hunghypebeast.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{orderId}/cancel")
    public OrderDetailDto cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}
