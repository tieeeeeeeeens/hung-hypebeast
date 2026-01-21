package com.hungshop.hunghypebeast.service.impl;

import com.hungshop.hunghypebeast.entity.EmailLog;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.repository.EmailLogRepository;
import com.hungshop.hunghypebeast.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@hunghypebeast.local}")
    private String fromAddress;

    @Value("${app.tracking.base-url:http://localhost:8080/api/orders/track}")
    private String trackingBaseUrl;

    @Override
    public EmailLog sendOrderConfirmation(Order order) {
        String recipient = order.getCustomerEmail();

        // Tránh gửi trùng nếu đã gửi thành công trước đó
        if (emailLogRepository.existsByOrderIdAndTypeAndStatus(
                order.getId(), EmailLog.EmailType.CONFIRMATION, EmailLog.EmailStatus.SENT)) {
            log.info("[Email] Skip sending confirmation for order {} because it was already SENT", order.getOrderNumber());
            return null;
        }

        EmailLog.EmailStatus status = EmailLog.EmailStatus.PENDING;
        String error = null;

        try {
            String subject = "[HUNG HYPEBEAST] Xác nhận đơn hàng " + order.getOrderNumber();
            String body = "Xin chào " + order.getCustomerName() + "!\n\n" +
                    "Cảm ơn bạn đã đặt hàng tại HUNG HYPEBEAST. Mã đơn của bạn là " + order.getOrderNumber() + ".\n" +
                    "Tổng tiền: " + order.getTotalAmount() + " VND.\n\n" +
                    "Chúng tôi sẽ xử lý đơn hàng trong thời gian sớm nhất.";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("[Email] Sent order confirmation to {} for order {}", recipient, order.getOrderNumber());
            status = EmailLog.EmailStatus.SENT;
        } catch (MailException ex) {
            log.error("[Email] Failed to send order confirmation to {}: {}", recipient, ex.getMessage());
            status = EmailLog.EmailStatus.FAILED;
            error = ex.getMessage();
        }

        EmailLog logEntry = EmailLog.builder()
                .order(order)
                .type(EmailLog.EmailType.CONFIRMATION)
                .recipientEmail(recipient)
                .status(status)
                .errorMessage(error)
                .build();

        return emailLogRepository.save(logEntry);
    }

    @Override
    public EmailLog sendOrderTracking(Order order, String trackingToken) {
        String recipient = order.getCustomerEmail();

        // Tránh gửi trùng tracking nếu đã gửi thành công trước đó
        if (emailLogRepository.existsByOrderIdAndTypeAndStatus(
                order.getId(), EmailLog.EmailType.TRACKING, EmailLog.EmailStatus.SENT)) {
            log.info("[Email] Skip sending tracking for order {} because it was already SENT", order.getOrderNumber());
            return null;
        }

        EmailLog.EmailStatus status = EmailLog.EmailStatus.PENDING;
        String error = null;

        try {
            String trackingUrl = trackingBaseUrl + "?token=" + trackingToken;
            String subject = "[HUNG HYPEBEAST] Theo dõi đơn hàng " + order.getOrderNumber();
            String body = "Xin chào " + order.getCustomerName() + "!\n\n" +
                    "Bạn có thể theo dõi trạng thái đơn hàng bằng token/đường dẫn sau: " + trackingUrl + "\n\n" +
                    "Cảm ơn bạn đã mua sắm tại HUNG HYPEBEAST.";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("[Email] Sent order tracking to {} for order {} with url {}", recipient, order.getOrderNumber(), trackingUrl);
            status = EmailLog.EmailStatus.SENT;
        } catch (MailException ex) {
            log.error("[Email] Failed to send order tracking to {}: {}", recipient, ex.getMessage());
            status = EmailLog.EmailStatus.FAILED;
            error = ex.getMessage();
        }

        EmailLog logEntry = EmailLog.builder()
                .order(order)
                .type(EmailLog.EmailType.TRACKING)
                .recipientEmail(recipient)
                .status(status)
                .errorMessage(error)
                .build();

        return emailLogRepository.save(logEntry);
    }
}
