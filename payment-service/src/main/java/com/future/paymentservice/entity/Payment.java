package com.future.paymentservice.entity;

import com.future.futurecommon.constant.BankCardType;
import com.future.futurecommon.constant.PaymentRefundStatus;
import com.future.futurecommon.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    private Long id; // 支付ID（雪花算法生成）

    @Column(name = "order_id", nullable = false)
    private Long orderId; // 订单ID

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING; // 支付状态

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_card_type", nullable = false)
    private BankCardType bankCardType; // 支付方式

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 支付金额

    @Column(name = "transaction_id", unique = true)
    private String transactionId; // 支付交易ID

    @Column(name = "refunded_amount", precision = 10, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO; // 已退款金额

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private PaymentRefundStatus refundStatus = PaymentRefundStatus.NOT_REFUNDED; // 退款状态

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 支付时间

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 更新时间
}
