package com.hungshop.hunghypebeast.service.impl;

import com.hungshop.hunghypebeast.dto.response.CategoryDto;
import com.hungshop.hunghypebeast.dto.response.ProductDto;
import com.hungshop.hunghypebeast.entity.Product;
import com.hungshop.hunghypebeast.exception.ResourceNotFoundException;
import com.hungshop.hunghypebeast.repository.CategoryRepository;
import com.hungshop.hunghypebeast.repository.ProductRepository;
import com.hungshop.hunghypebeast.service.ProductService;
import com.hungshop.hunghypebeast.mapping.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(ProductMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> getProductsByCategory(Long categoryId, Long minPrice, Long maxPrice, Pageable pageable) {
        Page<Product> page;
        if (minPrice != null && maxPrice != null) {
            page = productRepository.findDistinctByCategoryIdAndVariantsPriceBetween(categoryId, minPrice, maxPrice, pageable);
        } else {
            page = productRepository.findByCategoryId(categoryId, pageable);
        }
        List<ProductDto> content = page.getContent()
                .stream()
            .map(ProductMapper::toProductDto)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public ProductDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductMapper.toProductDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toProductDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductMapper.toProductDto(product);
    }
}
