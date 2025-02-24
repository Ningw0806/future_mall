package com.future.futurecommon.payload;

import com.future.futurecommon.constant.OrderStatus;
import com.future.futurecommon.constant.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderInfoDTO {
    private Long orderId;

    private Long userId;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private BankCardInfoDTO bankCardInfo;

    private LocalDateTime orderTime = LocalDateTime.now();
}
