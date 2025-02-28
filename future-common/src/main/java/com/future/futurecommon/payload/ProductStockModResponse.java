package com.future.futurecommon.payload;

import com.future.futurecommon.constant.ProductStockEventResponseType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockModResponse {
    private ProductStockEventResponseType eventType;
}
