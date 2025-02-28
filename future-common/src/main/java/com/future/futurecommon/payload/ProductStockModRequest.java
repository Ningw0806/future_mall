package com.future.futurecommon.payload;

import com.future.futurecommon.constant.ProductStockEventType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockModRequest {
    private List<ProductStockDTO> productStockDTOList;
    private ProductStockEventType productStockEventType;
}
