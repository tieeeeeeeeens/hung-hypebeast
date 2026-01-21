package com.hungshop.hunghypebeast.repository;

import com.hungshop.hunghypebeast.entity.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

	Page<EmailLog> findByOrderId(Long orderId, Pageable pageable);

	boolean existsByOrderIdAndTypeAndStatus(Long orderId, EmailLog.EmailType type, EmailLog.EmailStatus status);
}
