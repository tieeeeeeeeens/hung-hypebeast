package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.dto.response.EmailLogDto;
import com.hungshop.hunghypebeast.entity.EmailLog;
import com.hungshop.hunghypebeast.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/email-logs")
@RequiredArgsConstructor
public class AdminEmailLogController {

    private final EmailLogRepository emailLogRepository;

    @GetMapping
    public Page<EmailLogDto> listEmailLogs(@RequestParam(value = "orderId", required = false) Long orderId,
                                           Pageable pageable) {
        Page<EmailLog> page;
        if (orderId != null) {
            page = emailLogRepository.findByOrderId(orderId, pageable);
        } else {
            page = emailLogRepository.findAll(pageable);
        }

        List<EmailLogDto> dtos = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private EmailLogDto toDto(EmailLog log) {
        Long orderId = log.getOrder() != null ? log.getOrder().getId() : null;
        return EmailLogDto.builder()
                .id(log.getId())
                .orderId(orderId)
                .type(log.getType().name())
                .recipientEmail(log.getRecipientEmail())
                .status(log.getStatus().name())
                .errorMessage(log.getErrorMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
