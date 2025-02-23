package com.future.orderservice.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private OrderDTO order;
    private OrderAddressDTO address;
    private OrderCancellationDTO cancel;
}
