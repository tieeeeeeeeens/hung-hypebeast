package com.hungshop.hunghypebeast.service.impl;

import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.entity.OrderItem;
import com.hungshop.hunghypebeast.entity.Payment;
import com.hungshop.hunghypebeast.entity.ProductInventory;
import com.hungshop.hunghypebeast.exception.ResourceNotFoundException;
import com.hungshop.hunghypebeast.mapping.OrderMapper;
import com.hungshop.hunghypebeast.repository.OrderRepository;
import com.hungshop.hunghypebeast.repository.PaymentRepository;
import com.hungshop.hunghypebeast.repository.ProductInventoryRepository;
import com.hungshop.hunghypebeast.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductInventoryRepository productInventoryRepository;

    @Transactional
    @Override
    public OrderDetailDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
            String trackingToken = order.getTrackingToken() != null ? order.getTrackingToken().getToken() : null;
            return OrderMapper.toDetailDto(order, payment, trackingToken);
        }

        for (OrderItem item : order.getItems()) {
            Long variantId = item.getVariant().getId();

            ProductInventory inventory = productInventoryRepository
                    .findByVariantIdWithLock(variantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

            int quantity = item.getQuantity().intValue();
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
            inventory.setSoldQuantity(inventory.getSoldQuantity() - quantity);

            productInventoryRepository.save(inventory);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        String trackingToken = order.getTrackingToken() != null ? order.getTrackingToken().getToken() : null;
        return OrderMapper.toDetailDto(order, payment, trackingToken);
    }
}
