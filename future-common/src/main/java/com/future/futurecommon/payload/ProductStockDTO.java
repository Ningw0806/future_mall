package com.future.futurecommon.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockDTO {
    private Long productId;
    private int quantity;
    private boolean available;
}
