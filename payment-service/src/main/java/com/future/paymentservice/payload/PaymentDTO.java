package com.future.paymentservice.payload;

import com.future.futurecommon.constant.BankCardType;
import com.future.futurecommon.constant.PaymentRefundStatus;
import com.future.futurecommon.constant.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private PaymentStatus paymentStatus;
    private BankCardType bankCardType;
    private BigDecimal amount;
    private String transactionId;
    private BigDecimal refundedAmount;
    private PaymentRefundStatus refundStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", paymentStatus=" + paymentStatus +
                ", bankCardType=" + bankCardType +
                ", amount=" + amount +
                ", transactionId='" + transactionId + '\'' +
                ", refundedAmount=" + refundedAmount +
                ", refundStatus=" + refundStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
