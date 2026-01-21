package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderAutoCancelJob {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Value("${app.order.auto-cancel-minutes:60}")
    private long autoCancelMinutes;

    /**
     * Định kỳ quét các đơn PENDING quá lâu và tự động chuyển sang CANCELLED,
     * đồng thời trả lại tồn kho thông qua OrderService.cancelOrder().
     */
    @Scheduled(fixedDelayString = "60000") // mỗi 60 giây quét một lần
    public void cancelStalePendingOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusMinutes(autoCancelMinutes);

        List<Order> staleOrders = orderRepository
                .findByStatusAndCreatedAtBefore(Order.OrderStatus.PENDING, cutoff);

        if (staleOrders.isEmpty()) {
            return;
        }

        log.info("Found {} stale PENDING orders to auto-cancel (older than {} minutes)",
                staleOrders.size(), autoCancelMinutes);

        for (Order order : staleOrders) {
            try {
                orderService.cancelOrder(order.getId());
            } catch (Exception ex) {
                log.warn("Failed to auto-cancel order {}: {}", order.getId(), ex.getMessage());
            }
        }
    }
}
