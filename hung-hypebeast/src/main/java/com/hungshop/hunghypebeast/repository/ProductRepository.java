package com.hungshop.hunghypebeast.repository;

import com.hungshop.hunghypebeast.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findDistinctByCategoryIdAndVariantsPriceBetween(Long categoryId, Long minPrice, Long maxPrice, Pageable pageable);
}
