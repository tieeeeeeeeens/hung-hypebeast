package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.dto.response.OrderTrackingDto;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.mapping.OrderMapper;
import com.hungshop.hunghypebeast.entity.TrackingToken;
import com.hungshop.hunghypebeast.exception.ResourceNotFoundException;
import com.hungshop.hunghypebeast.repository.TrackingTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderTrackingController {

    private final TrackingTokenRepository trackingTokenRepository;

    @GetMapping("/track")
    public OrderTrackingDto trackOrder(@RequestParam("token") String token) {
        TrackingToken trackingToken = trackingTokenRepository
                .findByTokenAndExpiresAtAfter(token, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking token not found or expired"));

        Order order = trackingToken.getOrder();
        return OrderMapper.toTrackingDto(order, trackingToken.getToken());
    }
}
