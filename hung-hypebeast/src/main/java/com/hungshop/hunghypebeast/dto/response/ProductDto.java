package com.hungshop.hunghypebeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private List<ProductVariantDto> variants;
    private List<String> imageUrls;
}
