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
public class OrderTrackingDto {
    private String token;
    private String orderNumber;
    private String status;
    private Long totalAmount;
    private LocalDateTime createdAt;

    private String customerName;
    private String customerPhone;

    private List<OrderItemDto> items;
}
