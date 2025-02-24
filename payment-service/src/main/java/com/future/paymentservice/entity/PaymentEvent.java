package com.future.paymentservice.entity;

import com.future.futurecommon.constant.PaymentEventType;
import com.future.futurecommon.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    @Id
    private Long id; // 事件ID（雪花算法生成）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment; // 关联支付表

    @Column(name = "order_id", nullable = false)
    private Long orderId; // 订单ID，用于 Kafka 事件消息

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private PaymentEventType eventType; // 事件类型

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private PaymentStatus previousStatus; // 变更前状态

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private PaymentStatus newStatus; // 变更后状态

    @Column(name = "event_data", columnDefinition = "json")
    private String eventData; // 附加数据（如错误码、网关返回信息等）

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 事件创建时间
}
