package com.hungshop.hunghypebeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long variantId;
    private String sku;
    private String productName;
    private String size;
    private String color;
    private Long price;
    private Integer quantity;
    private Long lineTotal;

    private String stockStatus;

    private Integer maxAvailable;
}
