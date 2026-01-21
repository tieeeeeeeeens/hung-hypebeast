package com.hungshop.hunghypebeast.repository;

import com.hungshop.hunghypebeast.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    ProductVariant findBySku(String sku);
}
