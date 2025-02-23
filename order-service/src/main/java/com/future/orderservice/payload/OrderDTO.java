package com.future.orderservice.payload;

import com.future.futurecommon.constant.OrderStatus;
import com.future.futurecommon.constant.PaymentStatus;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    private Long userId;

    private OrderStatus status;

    private BigDecimal totalPrice;

    private Long addressId;

    private PaymentStatus paymentStatus;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private List<OrderItemDTO> orderItemList;

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId=" + id +
                ", userId=" + userId +
                ", orderStatus=" + status +
                ", totalPrice=" + totalPrice +
                ", addressId=" + addressId +
                ", paymentStatus=" + paymentStatus +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                ", orderItemList=" + orderItemList +
                '}';
    }
}
