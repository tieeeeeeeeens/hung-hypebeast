package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;

public interface OrderService {

    OrderDetailDto cancelOrder(Long orderId);
}
