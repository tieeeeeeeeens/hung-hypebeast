package com.hungshop.hunghypebeast.repository;

import com.hungshop.hunghypebeast.entity.ProductInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.variant.id = :variantId")
    Optional<ProductInventory> findByVariantIdWithLock(Long variantId);

    ProductInventory findByVariantId(Long variantId);
}
