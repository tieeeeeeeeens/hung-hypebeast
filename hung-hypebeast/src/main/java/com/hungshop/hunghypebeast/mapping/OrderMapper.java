package com.hungshop.hunghypebeast.mapping;

import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;
import com.hungshop.hunghypebeast.dto.response.OrderItemDto;
import com.hungshop.hunghypebeast.dto.response.OrderSummaryDto;
import com.hungshop.hunghypebeast.dto.response.OrderTrackingDto;
import com.hungshop.hunghypebeast.dto.response.PaymentInfoDto;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.entity.OrderItem;
import com.hungshop.hunghypebeast.entity.Payment;
import com.hungshop.hunghypebeast.entity.Product;
import com.hungshop.hunghypebeast.entity.ProductVariant;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderSummaryDto toSummaryDto(Order order) {
        if (order == null) return null;
        return OrderSummaryDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }

        public static OrderDetailDto toDetailDto(Order order, Payment payment, String trackingToken) {
        if (order == null) return null;
        List<OrderItem> items = order.getItems();
        List<OrderItemDto> itemDtos = items == null ? List.of() : items.stream()
                .map(OrderMapper::toItemDto)
                .collect(Collectors.toList());

        PaymentInfoDto paymentInfo = null;
        if (payment != null) {
            paymentInfo = PaymentInfoDto.builder()
                    .method(payment.getMethod().name())
                    .status(payment.getStatus().name())
                    .transactionId(payment.getTransactionId())
                    .sePayQrCode(payment.getSePayQrCode())
                    .sePayReference(payment.getSePayReference())
                    .build();
        }

        return OrderDetailDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .trackingToken(trackingToken)
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .customerAddress(order.getShippingAddress())
                .items(itemDtos)
                .payment(paymentInfo)
                .build();
    }

    public static OrderTrackingDto toTrackingDto(Order order, String token) {
        if (order == null) return null;
        List<OrderItem> items = order.getItems();
        List<OrderItemDto> itemDtos = items == null ? List.of() : items.stream()
                .map(OrderMapper::toItemDto)
                .collect(Collectors.toList());

        return OrderTrackingDto.builder()
                .token(token)
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .items(itemDtos)
                .build();
    }

    public static OrderItemDto toItemDto(OrderItem orderItem) {
        ProductVariant variant = orderItem.getVariant();
        Product product = variant.getProduct();

        long price = orderItem.getPriceAtTime();
        long quantity = orderItem.getQuantity();
        long lineTotal = price * quantity;

        return OrderItemDto.builder()
                .variantId(variant.getId())
                .sku(variant.getSku())
                .productName(product.getName())
                .size(variant.getSize())
                .color(variant.getColor())
                .quantity((int) quantity)
                .price(price)
                .lineTotal(lineTotal)
                .build();
    }
}