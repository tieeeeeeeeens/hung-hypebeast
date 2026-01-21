package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.dto.response.CategoryDto;
import com.hungshop.hunghypebeast.dto.response.ProductDto;
import com.hungshop.hunghypebeast.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories() {
        return productService.getAllCategories();
    }

    @GetMapping("/categories/{categoryId}")
    public Page<ProductDto> getProductsByCategory(@PathVariable Long categoryId,
                                                  @RequestParam(value = "minPrice", required = false) Long minPrice,
                                                  @RequestParam(value = "maxPrice", required = false) Long maxPrice,
                                                  Pageable pageable) {
        return productService.getProductsByCategory(categoryId, minPrice, maxPrice, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductDetail(@PathVariable Long productId) {
        return productService.getProductDetail(productId);
    }
}
