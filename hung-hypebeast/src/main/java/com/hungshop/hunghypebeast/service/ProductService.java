package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.dto.response.CategoryDto;
import com.hungshop.hunghypebeast.dto.response.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    List<CategoryDto> getAllCategories();

    Page<ProductDto> getProductsByCategory(Long categoryId, Long minPrice, Long maxPrice, Pageable pageable);

    ProductDto getProductDetail(Long productId);

    List<ProductDto> getAllProducts();

    ProductDto getProductById(Long productId);
}
