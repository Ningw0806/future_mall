package com.future.orderservice.entity;

import com.future.futurecommon.constant.OrderEventType;
import com.future.futurecommon.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {

    @Id
    private Long id; // Snowflake ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private OrderEventType orderEventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private OrderStatus newStatus;

    @Column(name = "event_data", columnDefinition = "JSON")
    private String eventData; // JSON format for extra details

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Override
    public String toString() {
        return "OrderEvent{" +
                "id=" + id +
                ", order=" + order +
                ", orderEventType=" + orderEventType +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", eventData='" + eventData + '\'' +
                ", createAt=" + createAt +
                '}';
    }
}
