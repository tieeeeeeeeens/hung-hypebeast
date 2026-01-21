package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.dto.request.AddCartItemRequest;
import com.hungshop.hunghypebeast.dto.CartContext;
import com.hungshop.hunghypebeast.dto.response.CartDto;
import com.hungshop.hunghypebeast.dto.request.UpdateCartItemRequest;

public interface CartService {

    CartDto getCart(CartContext context);

    CartDto addItem(CartContext context, AddCartItemRequest request);

    CartDto updateItem(CartContext context, Long cartItemId, UpdateCartItemRequest request);

    CartDto removeItem(CartContext context, Long cartItemId);

    void clearCart(CartContext context);
}
