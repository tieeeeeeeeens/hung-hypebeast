package com.hungshop.hunghypebeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long variantId;
    private String sku;
    private String productName;
    private String size;
    private String color;
    private Integer quantity;
    private Long price;
    private Long lineTotal;
}
