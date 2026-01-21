package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.entity.Payment;
import com.hungshop.hunghypebeast.repository.OrderRepository;
import com.hungshop.hunghypebeast.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PaymentResultController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping(value = "/payment/success", produces = MediaType.TEXT_HTML_VALUE)
    public String paymentSuccess(@RequestParam(value = "order", required = false) String orderNumber) {
        String title = "Thanh toán thành công";

        if (orderNumber != null && !orderNumber.isBlank()) {
            Optional<Order> optionalOrder = orderRepository.findByOrderNumber(orderNumber);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();

                paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {
                    if (payment.getMethod() == Payment.PaymentMethod.SEPAY) {
                        payment.setStatus(Payment.PaymentStatus.COMPLETED);
                        paymentRepository.save(payment);
                    }
                });

                if (order.getStatus() == Order.OrderStatus.PENDING || order.getStatus() == Order.OrderStatus.CONFIRMED) {
                    order.setStatus(Order.OrderStatus.PAID);
                    orderRepository.save(order);
                }
            }
        }

        String body = orderNumber != null
                ? "Đơn hàng " + orderNumber + " đã thanh toán thành công."
                : "Thanh toán thành công.";

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + title + "</title></head>" +
                "<body><h2>" + title + "</h2><p>" + body + "</p></body></html>";
    }

    @GetMapping(value = "/payment/error", produces = MediaType.TEXT_HTML_VALUE)
    public String paymentError() {
        String title = "Thanh toán thất bại";
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + title + "</title></head>" +
                "<body><h2>" + title + "</h2><p>Vui lòng thử lại hoặc chọn phương thức khác.</p></body></html>";
    }

    @GetMapping(value = "/payment/cancel", produces = MediaType.TEXT_HTML_VALUE)
    public String paymentCancel() {
        String title = "Đã hủy thanh toán";
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + title + "</title></head>" +
                "<body><h2>" + title + "</h2><p>Bạn đã hủy giao dịch. Nếu đây là nhầm lẫn, vui lòng đặt lại đơn.</p></body></html>";
    }
}
