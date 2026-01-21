package com.hungshop.hunghypebeast.mapping;

import com.hungshop.hunghypebeast.dto.response.CartDto;
import com.hungshop.hunghypebeast.dto.response.CartItemDto;
import com.hungshop.hunghypebeast.entity.Cart;
import com.hungshop.hunghypebeast.entity.CartItem;
import com.hungshop.hunghypebeast.entity.Product;
import com.hungshop.hunghypebeast.entity.ProductVariant;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    private CartMapper() {
    }

    public static CartDto toCartDto(Cart cart) {
        if (cart == null) return null;

        List<CartItemDto> itemDtos = cart.getItems() == null ? List.of() : cart.getItems().stream()
                .map(CartMapper::toCartItemDto)
                .collect(Collectors.toList());

        int totalQuantity = itemDtos.stream()
                .mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0)
                .sum();
        long totalAmount = itemDtos.stream()
                .mapToLong(i -> i.getLineTotal() != null ? i.getLineTotal() : 0L)
                .sum();

        return CartDto.builder()
                .id(cart.getId())
                .sessionId(cart.getSessionId())
                .items(itemDtos)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .build();
    }

    public static CartItemDto toCartItemDto(CartItem item) {
        ProductVariant variant = item.getVariant();
        Product product = variant.getProduct();

        long price = variant.getPrice();
        int quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 0;
        long lineTotal = price * quantity;

        return CartItemDto.builder()
                .id(item.getId())
                .variantId(variant.getId())
                .sku(variant.getSku())
                .productName(product.getName())
                .size(variant.getSize())
                .color(variant.getColor())
                .price(price)
                .quantity(quantity)
                .lineTotal(lineTotal)
                .build();
    }
}