package com.hungshop.hunghypebeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDto {
    private Long id;
    private String sku;
    private String size;
    private String color;
    private Long price;
    private Integer availableQuantity;
}
