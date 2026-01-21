package com.hungshop.hunghypebeast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfoDto {
    private String method; // COD, SEPAY
    private String status;
    private String transactionId;
    private String sePayQrCode;
    private String sePayReference;
}
