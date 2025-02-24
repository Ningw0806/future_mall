package com.future.futurecommon.payload;

import com.future.futurecommon.constant.PaymentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResultDTO {
    private long orderId;

    private PaymentStatus paymentStatus;

    private String message;
}
