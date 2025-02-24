package com.future.paymentservice.payload;

import com.future.futurecommon.constant.RefundStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRefundDTO {
    private Long id;
    private Long paymentId;
    private RefundStatus refundStatus;
    private BigDecimal refundAmount;
    private String refundTransactionId;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
