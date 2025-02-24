package com.future.paymentservice.payload;

import com.future.futurecommon.constant.BankCardType;
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
    private String userFirstName;
    private String userLastName;
    private String cardNumber;
    private String nameOnCard;
    private String securityCode;
    private BankCardType bankCardType;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
