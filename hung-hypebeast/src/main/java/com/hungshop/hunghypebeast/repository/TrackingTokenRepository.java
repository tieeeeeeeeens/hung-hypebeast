package com.hungshop.hunghypebeast.repository;

import com.hungshop.hunghypebeast.entity.TrackingToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TrackingTokenRepository extends JpaRepository<TrackingToken, Long> {
    Optional<TrackingToken> findByToken(String token);

    Optional<TrackingToken> findByTokenAndExpiresAtAfter(String token, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime before);
}
