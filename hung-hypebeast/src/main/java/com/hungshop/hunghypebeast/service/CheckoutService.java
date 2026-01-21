package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.dto.CartContext;
import com.hungshop.hunghypebeast.dto.request.CheckoutRequest;
import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;

public interface CheckoutService {

    OrderDetailDto checkout(CartContext context, CheckoutRequest request);
}
