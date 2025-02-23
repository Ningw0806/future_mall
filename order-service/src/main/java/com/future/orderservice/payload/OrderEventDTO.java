package com.future.orderservice.payload;

import com.future.futurecommon.constant.OrderEventType;
import com.future.futurecommon.constant.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEventDTO {
    private Long eventId;
    private Long orderId;
    private OrderEventType eventType;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private String eventData; // JSON as a String
    private LocalDateTime createdAt;
}
