package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.config.CartContextHolder;
import com.hungshop.hunghypebeast.dto.CartContext;
import com.hungshop.hunghypebeast.dto.request.CheckoutRequest;
import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;
import com.hungshop.hunghypebeast.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    private CartContext currentContext() {
        CartContext context = CartContextHolder.getContext();
        if (context == null) {
            throw new IllegalArgumentException("CartContext is not available for this request");
        }
        return context;
    }

    @PostMapping
    public OrderDetailDto checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(currentContext(), request);
    }
}
