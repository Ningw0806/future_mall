package com.future.orderservice.payload;

import com.future.futurecommon.constant.OrderStatus;
import com.future.futurecommon.constant.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderQueryDTO {
    private long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
}
