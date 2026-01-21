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
public class ReservationDto {
    private Long id;
    private Long variantId;
    private String sku;
    private String status;
    private Integer quantity;
    private LocalDateTime expiresAt;
}
