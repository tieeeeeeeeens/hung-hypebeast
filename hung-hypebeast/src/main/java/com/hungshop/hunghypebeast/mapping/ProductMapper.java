package com.hungshop.hunghypebeast.mapping;

import com.hungshop.hunghypebeast.dto.response.CategoryDto;
import com.hungshop.hunghypebeast.dto.response.ProductDto;
import com.hungshop.hunghypebeast.dto.response.ProductVariantDto;
import com.hungshop.hunghypebeast.entity.Category;
import com.hungshop.hunghypebeast.entity.Product;
import com.hungshop.hunghypebeast.entity.ProductImage;
import com.hungshop.hunghypebeast.entity.ProductInventory;
import com.hungshop.hunghypebeast.entity.ProductVariant;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    private ProductMapper() {
    }

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public static ProductDto toProductDto(Product product) {
        if (product == null) return null;

        List<ProductVariantDto> variantDtos = product.getVariants() == null ? List.of() :
                product.getVariants().stream()
                        .map(ProductMapper::toVariantDto)
                        .collect(Collectors.toList());

        List<String> imageUrls = product.getImages() == null ? List.of() :
                product.getImages().stream()
                        .sorted((a, b) -> {
                            Integer sa = a.getSortOrder() != null ? a.getSortOrder() : Integer.MAX_VALUE;
                            Integer sb = b.getSortOrder() != null ? b.getSortOrder() : Integer.MAX_VALUE;
                            int cmp = sa.compareTo(sb);
                            if (cmp != 0) {
                                return cmp;
                            }
                            Long ida = a.getId() != null ? a.getId() : Long.MAX_VALUE;
                            Long idb = b.getId() != null ? b.getId() : Long.MAX_VALUE;
                            return ida.compareTo(idb);
                        })
                        .map(ProductImage::getUrl)
                        .collect(Collectors.toList());

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .variants(variantDtos)
                .imageUrls(imageUrls)
                .build();
    }

    public static ProductVariantDto toVariantDto(ProductVariant variant) {
        if (variant == null) return null;
        Integer availableQuantity = null;
        ProductInventory inventory = variant.getInventory();
        if (inventory != null && inventory.getAvailableQuantity() != null) {
            availableQuantity = inventory.getAvailableQuantity().intValue();
        }

        return ProductVariantDto.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .size(variant.getSize())
                .color(variant.getColor())
                .price(variant.getPrice())
                .availableQuantity(availableQuantity)
                .build();
    }
}