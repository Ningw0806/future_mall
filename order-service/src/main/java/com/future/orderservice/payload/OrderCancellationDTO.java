package com.future.orderservice.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancellationDTO {
    private Long id;
    private Long orderId;

    @NotBlank(message = "Cancellation reason must not be blank")
    private String reason;
    private LocalDateTime cancelledAt;
}
