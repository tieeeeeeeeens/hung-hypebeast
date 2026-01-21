package com.hungshop.hunghypebeast.repository;

import com.hungshop.hunghypebeast.entity.Cart;
import com.hungshop.hunghypebeast.entity.ProductVariant;
import com.hungshop.hunghypebeast.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.status = 'RESERVED' AND r.expiresAt < :now")
    List<Reservation> findExpiredReservations(LocalDateTime now);

    Optional<Reservation> findByIdAndStatus(Long id, Reservation.ReservationStatus status);

    Optional<Reservation> findByCartAndVariantAndStatus(Cart cart, ProductVariant variant, Reservation.ReservationStatus status);

    List<Reservation> findByCartAndStatus(Cart cart, Reservation.ReservationStatus status);
}
