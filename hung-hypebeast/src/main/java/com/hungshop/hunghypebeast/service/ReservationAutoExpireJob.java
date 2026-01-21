package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.entity.ProductInventory;
import com.hungshop.hunghypebeast.entity.Reservation;
import com.hungshop.hunghypebeast.repository.ProductInventoryRepository;
import com.hungshop.hunghypebeast.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationAutoExpireJob {

    private final ReservationRepository reservationRepository;
    private final ProductInventoryRepository productInventoryRepository;

    @Scheduled(fixedDelayString = "60000") // mỗi 60 giây quét một lần
    @Transactional
    public void expireReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expired = reservationRepository.findExpiredReservations(now);

        if (expired.isEmpty()) {
            return;
        }

        log.info("Found {} expired reservations to process", expired.size());

        List<ProductInventory> inventoriesToSave = new ArrayList<>();

        for (Reservation reservation : expired) {
            Long variantId = reservation.getVariant().getId();
            ProductInventory inventory = productInventoryRepository.findByVariantIdWithLock(variantId)
                    .orElse(null);

            if (inventory == null) {
                log.warn("No inventory found for variant {} while expiring reservation {}", variantId, reservation.getId());
            } else {
                long qty = reservation.getQuantity();

                long available = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0L;
                long reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0L;

                inventory.setAvailableQuantity(available + qty);
                long newReserved = reserved - qty;
                if (newReserved < 0) {
                    newReserved = 0;
                }
                inventory.setReservedQuantity(newReserved);

                inventoriesToSave.add(inventory);
            }

            reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
        }

        if (!inventoriesToSave.isEmpty()) {
            productInventoryRepository.saveAll(inventoriesToSave);
        }

        reservationRepository.saveAll(expired);

        log.info("Expired {} reservations", expired.size());
    }
}
