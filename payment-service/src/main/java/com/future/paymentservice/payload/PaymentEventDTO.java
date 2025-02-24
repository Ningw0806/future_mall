package com.future.paymentservice.payload;

import com.future.futurecommon.constant.PaymentEventType;
import com.future.futurecommon.constant.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEventDTO {
    private Long id;
    private Long paymentId;
    private Long orderId;
    private PaymentEventType eventType;
    private PaymentStatus previousStatus;
    private PaymentStatus newStatus;
    private String eventData;
    private LocalDateTime createdAt;
}
