package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.config.CartContextHolder;
import com.hungshop.hunghypebeast.dto.request.AddCartItemRequest;
import com.hungshop.hunghypebeast.dto.CartContext;
import com.hungshop.hunghypebeast.dto.response.CartDto;
import com.hungshop.hunghypebeast.dto.request.UpdateCartItemRequest;
import com.hungshop.hunghypebeast.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private CartContext currentContext() {
        CartContext context = CartContextHolder.getContext();
        if (context == null) {
            throw new IllegalArgumentException("CartContext is not available for this request");
        }
        return context;
    }

    @GetMapping
    public CartDto getCart() {
        return cartService.getCart(currentContext());
    }

    @PostMapping("/items")
    public CartDto addItem(@Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(currentContext(), request);
    }

    @PutMapping("/items/{itemId}")
    public CartDto updateItem(@PathVariable Long itemId,
                              @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(currentContext(), itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartDto removeItem(@PathVariable Long itemId) {
        return cartService.removeItem(currentContext(), itemId);
    }

    @DeleteMapping
    public void clearCart() {
        cartService.clearCart(currentContext());
    }
}
