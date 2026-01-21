package com.hungshop.hunghypebeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {
    private Long id;
    private String orderNumber;
    private String status;
    private Long totalAmount;
    private LocalDateTime createdAt;

    private String trackingToken;

    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;

    private List<OrderItemDto> items;
    private PaymentInfoDto payment;
}
