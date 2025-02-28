package com.future.futurecommon.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockRequest {
    private List<ProductStockDTO> productStockDTOList;
}

