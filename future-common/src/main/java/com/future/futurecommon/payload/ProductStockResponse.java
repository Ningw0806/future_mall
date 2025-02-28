package com.future.futurecommon.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockResponse {
    private List<ProductStockDTO> productStockDTOList;
    private boolean result;
}
