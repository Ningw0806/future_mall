package com.future.orderservice.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private OrderDTO order;
    private OrderAddressDTO address;
}