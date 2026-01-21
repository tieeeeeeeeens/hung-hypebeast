package com.hungshop.hunghypebeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLogDto {
    private Long id;
    private Long orderId;
    private String type;
    private String recipientEmail;
    private String status;
    private String errorMessage;
    private LocalDateTime createdAt;
}
